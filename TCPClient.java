import java.io.*;
import java.net.*;

public class TCPClient{

	public static void main(String argv[]) throws IOException{
		String hostname;
		String port;
		String endOfMessage = "Escape";
		boolean appOn = true;
		boolean openChat = false;
		String user;
		String chattingWith
		boolean usernameSet =false;
		//prompt user for hostname
		//prompt user for port number
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter the hostname of the server");
		hostname = inFromUser.readLine();
		System.out.println("Please enter the port number of the server");
		port = inFromUser.readLine();

		//establish connection to server
		Socket clientSocket = new Socket(hostname, Integer.parseInt(port)); 
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

		//Inform user of options
		System.out.println("To end a message type 'Escape' on a new line")
		System.out.println("Enter: 'set username' followed by the username you wish to have on a new line ")
		System.out.println("Enter 'set correspondant' followed by the username you with to connect with on a new line")
		System.out.println("To end connection enter: 'end connection'")
		while(appOn){
			System.out.println("Enter a command or a message");
			String input = inFromUser.readLine();
			if(input.equals("set username")){
				user = inFromUser.readLine();
				outToServer.write("set username: " + user); //Figure out what listener means
				String response = inFromServer().readLine();
				System.out.println(response); //print response from the server of whether or not it was set
				if(repsonse.eqauls("successfully set"))
					usernameSet=true;
			}
			else if(input.equals("end connection")){
				clientSocket.close();   
				appOn=false;
			}
			else if(input.equals("set correspondant")){
				//set the correspondent if they exist in the server
				//Figure out what listener means-> I think it means set its own username as the correspondent
				//
			}
			else if(input.equals(endOfMessage));
			else{//assume its a message
				StringBuilder builder = new StringBuilder();
				builder.append(input);
				while(!input.equals(endOfMessage)){
					builder.append('\n');
					input=inFromUser.readLine();
				}
			}
		}
		//control msg
		//specify username of client, send to server to establish connection

		//specify the username of person to chat to or Listener
			//can only chat with one at a time
			//ends chat when changes to new person

		//-1 = end of message
		//make sure no overlap between reivece and sent messages

		//finish the chat and exit the app

		//unless input is a control command assume it is a text to correspondant
		//Listener the message goes back to the remote user that initiated chat




	}
}