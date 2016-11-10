import java.io.*; 
import java.net.*; 
import java.io.*;
import java.net.*;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.LinkedList;
class chatd { 

  private static final int PORT = 5037;
  //keeps track of active users
  private static HashSet<String> users = new HashSet<String>();

  //keep track of chat connections between clients
  private static LinkedList<Connection> chats = new LinkedList<Connection>();

  //Keeps track of input/output streams to clients
  private static LinkedList<ServerConnection> clients = new LinkedList<ServerConnection>();

  /*Sets up a new thread for every client connection*/
  public static void main(String argv[]) throws Exception {
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

  /*Class to run each client connection*/
  private static class ConnectionThread extends Thread{
      public String username = "";
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

          String proposedUsername = "not set"; 
          String response = " -"; 

          //Print out the host name the server can be identified by
          System.out.println(InetAddress.getLocalHost().getHostName());

          //While the username in not set yet, make client pick one
          while(username.equals("")){
          proposedUsername = inFromClient.readLine(); 

          synchronized (users){/*Checks to see if name is already taken*/
              if(!users.contains(proposedUsername)){ //If its availible, set it as the username
                this.username= proposedUsername;
                users.add(proposedUsername);
                response= "SUCCESSFULLY SET USERNAME";
                System.out.println("New user: "+ this.username);
                System.out.println(response);
                outToClient.writeBytes(response + '\n');
              }
              else{ //Reject if already used
                response = "UNSUCCESSFUL ATTEMPT TO SET USERNAME";
                System.out.println(response);
                outToClient.writeBytes(response + '\n');
              }
            }
          }
          Boolean applicationOn=true; //Turn other control options on 
          String controlMSG = inFromClient.readLine(); //Recieve controlmsg

          while(applicationOn){
            if(controlMSG.equals("ENDCONNECTION")){ //if the user wants to disconnect
                applicationOn=false;
                synchronized (users){
                  users.remove(username); //make username available again
                }
                //remove all traces of user
                for(int i = 0; i< chats.size();i++){
                  if(chats.get(i).sender.name.equals(username)){
                    chats.remove(i);
                  }
                }
                //remove all traces of user
                for(int i = 0; i< clients.size();i++){
                  if(clients.get(i).name.equals(username)){
                    clients.remove(i);
                  }
                }
                System.out.println("Connection with user: " + username + " is terminated"); //notify server of changes
                socket.close();
            }

            else if(controlMSG.equals("SETUSERNAME")){ //the client wants to change their username
              boolean newNameSet=false;
              System.out.println("Setting new username for: " + username);
                while(!newNameSet){
                  synchronized (users){
                  proposedUsername = inFromClient.readLine(); 
                  System.out.println("TRY: "+ proposedUsername);
                  if(!users.contains(proposedUsername)){
                    users.remove(username);
                    //remove all traces of user
                  for(int i = 0; i< chats.size();i++){
                    if(chats.get(i).sender.name.equals(username)){
                      chats.remove(i);
                    }
                  }
                  //remove all traces of user
                  for(int i = 0; i< clients.size();i++){
                    if(clients.get(i).name.equals(username)){
                      clients.remove(i);
                    }
                  }
                    this.username= proposedUsername; //create new username
                    users.add(proposedUsername);
                    response= "SUCCESSFULLY SET USERNAME";
                    System.out.println("New user: "+ this.username);
                    System.out.println(response);
                    outToClient.writeBytes(response + '\n');
                    newNameSet=true;
                  
                }
                  else{
                    response = "UNSUCCESSFUL ATTEMPT TO SET USERNAME";
                    System.out.println(response);
                    outToClient.writeBytes(response + '\n');
                  }
                }
                
              } 
            }

            else if(controlMSG.equals("SETUPCHAT")){ //Create a new connecion between clients, remove old ones
              String output= "-";
              String partner = inFromClient.readLine();
              System.out.println("Requested partner " + partner);
              if(partner.equals("Listener")){
                //Remove exitsting connection
                for(int i = 0; i< chats.size();i++){
                  if(chats.get(i).sender.name.equals(username)){
                    chats.remove(i);
                  }
                }
                //setup connection with self
                ServerConnection userconnection = findConnection(username,clients);
                chats.add(new Connection(userconnection,userconnection));
                output = "CHAT SUCCESSFULLY SET UP";
              }

              else if(users.contains(partner)){
                if(users.contains(partner)){
                  //remove existing connection
                  for(int i = 0; i< chats.size();i++){
                    if(chats.get(i).sender.name.equals(username)){
                      chats.remove(i);
                    }
                  }
                  //set up new connection
                  ServerConnection userconnection = findConnection(username,clients);
                  ServerConnection partnerconnection = findConnection(partner,clients);
                  chats.add(new Connection(userconnection,partnerconnection));
                  output = "CHAT SUCCESSFULLY SET UP";
                }
              }
              else{
                output="UNSUCCESSFUL INVAILD USERNAME FOR CLIENT";
              }
              outToClient.writeBytes(output+ '\n');
            }

            else{ //assume its an incoming message
              boolean activeChat =false;
              Connection chat= null;
              for(int i =0; i< chats.size();i++){
                if(chats.get(i).sender.name.equals(username)){
                  activeChat=true;
                  chat = chats.get(i);
                }
              } /*Make sure their is someone to send it to*/
              if(activeChat){
                chat.reciever.toClient.writeBytes("RECIEVE MSG" + '\n');
                chat.reciever.toClient.writeBytes("MESSAGE FROM: "+ username + '\n');
                while(!controlMSG.equals("ESCAPE")){
                  chat.reciever.toClient.writeBytes(controlMSG + '\n');
                  controlMSG= inFromClient.readLine();
                }
                chat.reciever.toClient.writeBytes("ESCAPE" + '\n');
                response = "MESSAGE SENT TO: "+ chat.reciever.name;
              }
              else{ // RETURN COULDN"T SEND MSG, NO ONE TO RECIEVE
              System.out.println("gets to message");
                response = "COULDN'T SEND MSG, NOT CHATTING WITH ANYONE";
              }
              outToClient.writeBytes(response +'\n');
            }
          }
          controlMSG = inFromClient.readLine();
        }
 
    catch (IOException e) {
                System.out.println(e);
    }
  }
}

  /*Method to find connections between client and server*/
  public static ServerConnection findConnection(String username,LinkedList<ServerConnection> list){
    for(int i=0;i<list.size();i++){
      if(list.get(i).name.equals(username)){
        return list.get(i);
      }
    }
     return null;
  }

  /*Class to store a connection between clients*/
  private static class Connection{
    public ServerConnection sender;
    public ServerConnection reciever;

    public Connection(ServerConnection s,ServerConnection r){
      this.sender=s;
      this.reciever=r;
    }
}
  /*Class to store a connection to the client*/
  private static class ServerConnection{
    public String name;
    public DataOutputStream toClient;

    public ServerConnection(String s, DataOutputStream toC){
      this.name=s;
      this.toClient=toC;
    }
}

}







