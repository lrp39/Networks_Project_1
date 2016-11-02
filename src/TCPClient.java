import java.io.*;
import java.net.*;

public class TCPClient{

	public static void main(String argv[]) throws IOException{
		String hostname;
		String port;
		String endOfMessage = "-1";
		boolean appOn = true;
		//prompt user for hostname
		//prompt user for port number
		DataOutputStream outToUser = new DataOutputStream(System.out);
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please enter the hostname of the server");
		hostname = inFromUser.readLine();
		System.out.println("Please enter the port number of the server");
		port = inFromUser.readLine();
		//establish connection to server
		Socket clientSocket = new Socket(hostname, Integer.parseInt(port)); 
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 


		while(appOn){
			String controlMsg = inFromUser.readLine();
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