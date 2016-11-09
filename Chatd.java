import java.io.*;
import java.net.*;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.LinkedList;
/*Make sure to go back to client and have them be removed from the users when they exit the app*/
/*Maybe make it so if one user sets a chat with another they automatically are both chatting with eachother*/
public class Chatd {
	private static final int PORT = 5537;
	//keeps track of active users
	private static HashSet<String> users = new HashSet<String>();

	//keep track of chat connections between clients
	private static LinkedList<Connection> connections = new LinkedList<Connection>();

	//Keeps track of input/output streams to clients
	private static LinkedList<Clients> clients = new LinkedList<Clients>();

	public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(PORT);
		System.out.println(InetAddress.getLocalHost().getHostName());
		try {
            while (true) {
                new ConnectionThread(welcomeSocket.accept()).start();
            }
        } finally {
            welcomeSocket.close();
        }
    }

	private static class Clients{
		public String username;
		public BufferedReader inFromClient;
		public DataOutputStream outToClient;

		public Clients(String name, BufferedReader in, DataOutputStream out){
			this.username=name;
			this.inFromClient=in;
			this.outToClient=out;
		}
	}

	private static class Connection{
		public String sender;
		public String reciever;
		public BufferedReader inStream ;
		public DataOutputStream outStream ;

		public Connection(String sender, String reciever, BufferedReader in, DataOutputStream out){
			this.sender=sender;
			this.reciever=reciever;
			this.inStream=in;
			this.outStream=out;
		}
	}

	private static class ConnectionThread extends Thread{
		  public String username;
		  public Socket socket;
		  public BufferedReader inFromClient;
		  public DataOutputStream outToClient;

		  public ConnectionThread(Socket socket){
		    this.socket = socket;
		  }

		  public void run(){

		    try{
		    	inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            outToClient = new DataOutputStream(socket.getOutputStream());

	            boolean nameSet=false;
	            boolean connectionActive=true;
	            boolean currentConnection=false;
	            Connection myConnection=null;
	            while(connectionActive){
	            	//check on active connection, to see if the other person has been dropped
	            	if(!connections.contains(myConnection)){
	            		currentConnection=false;
	            		myConnection=null;
	            	}
		            //Wait until user sets username
	            	String input = inFromClient.readLine();
		            if(input.equals("SETUSERNAME")){
		            	if(nameSet){
		            		outToClient.writeChars("Username was already declared");
		            	}
		            	else{
		            		while(!nameSet){
		            			String name = inFromClient.readLine();
		            			synchronized (users){
		            				if(!users.contains(name)){
		            					username= name;
		            					users.add(name);
		            					Clients c = new Clients(username,inFromClient, outToClient);
		            					clients.add(c);
		            					nameSet=true;
		            					outToClient.writeChars("USERNAME SUCCESSFULLY SET");
		            				}

		            			}
		            		}
		            	}
		            }
		            else if(input.equals("SETCORRESPONDENT")&& !currentConnection){
		            	String partner = inFromClient.readLine();
		            	//Add in Listener condition
		            	if(users.contains(partner)){
		            		//find socket input/output of partner
		            		BufferedReader partnerInput=lookupClientInputStream(username, clients);
		            		DataOutputStream partnerOutput = lookupClientOutputStream(partner, clients);

		            		//if there is a previous connection delete it
		            		for(int i =0; i < connections.size();i++){
		            			if(connections.get(i).sender.equals(username))
		            				connections.remove(i);
		            		}
		            		//set up connection between the two
		            		Connection c = new Connection(username,partner,partnerInput,partnerOutput);
		            		myConnection=c;
		            		connections.add(c);
		            		currentConnection=true;
		            	}
		            	else{
		            		outToClient.writeChars("NOT A VALID USER");
		            	}

		            }
		            else if(input.equals("END CONNECTION")){
		            	//remove username, client and connections involving them.
		            	users.remove(username);
		            	for(int i =0; i < connections.size();i++){
	            			if(connections.get(i).sender.equals(username) || connections.get(i).reciever.equals(username))
	            				connections.remove(i);
	            		}
		            	for(int i =0; i < clients.size();i++){
	            			if(clients.get(i).username.equals(username))
	            				clients.remove(i);
	            		}
		            	socket.close();
		            	connectionActive=false;

		            }
		            else{ //input must be a message
		            	//Make sure they have someone to talk to else error
		            	if(currentConnection){
		            		StringBuilder builder = new StringBuilder();
							while(!input.equals("ESCAPE")){
								builder.append(input).append("\n");
								input = inFromClient.readLine();
							}
							builder.append("ESCAPE");
							myConnection.outStream.writeChars("MSG FROM: "+username+"TO: "+ myConnection.reciever + "/n" + builder.toString());
		            	}
		            	else
		            		outToClient.writeChars("NO ONE TO SEND THE MESSAGE TO");
		            }
		         }

		    }
		    catch (IOException e) {
                System.out.println(e);
		}
	}


		private DataOutputStream lookupClientOutputStream(String partner, LinkedList<Clients> clients) {
			for(int i=0;i<clients.size();i++){
				if(clients.get(i).username.equals(partner)){
					return clients.get(i).outToClient;
				}
			}
			return null;
		}

		private BufferedReader lookupClientInputStream(String partner, LinkedList<Clients> clients) {
			for(int i=0;i<clients.size();i++){
				if(clients.get(i).username.equals(partner)){
					return clients.get(i).inFromClient;
				}
			}
			return null;
		}

	}
}
