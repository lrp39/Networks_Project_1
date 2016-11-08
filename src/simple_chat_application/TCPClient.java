package simple_chat_application;
import java.io.*;
import java.net.*;

public class Client {


	public static void main(String[] args) throws IOException{
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		//Prompt user for hostname and port of server they wish to connect with
		System.out.println("Please enter a hostname");
		String hostname = inFromUser.readLine();

		System.out.println("Please enter a port number");
		int port = Integer.parseInt(inFromUser.readLine());

		//create a TCP connection to the server
		Socket clientSocket = new Socket(hostname,port);

		//create an input and output to the server
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

		//inform users of their options
		System.out.println("Enter 'SETCORRESPONDENT' followed by the username you with to connect with on a new line");
		System.out.println("To end connection enter: 'ENDCONNECTION'");
		System.out.println("Any other input will be assumed as a message for your correspondent");
		System.out.println("To end a message type 'ESCAPE' on a new line");

		//
		boolean applicationOn = true;
		while(applicationOn){
			String username = null;
			String correspondent = null;
			boolean usernameSet= false;
			boolean correspondentSet= false;
			System.out.println("Enter a command or a message");
			String input = inFromUser.readLine();
			if(input.equals("end connection")){
				clientSocket.close();
				applicationOn=false;
			}
			else if(input.equals("SETUSERNAME")){
				//Keeps prompting user for a valid username to give server
				while(!usernameSet){
					System.out.println("Please enter a username");
					String name= inFromUser.readLine();
					outToServer.writeChars("SETUSERNAME:"+ name);
					String response = inFromServer.readLine();
					if(response.equals("USERNAME SUCCESSFULLY SET")){
						username= name;
						usernameSet=true;
					}
				}
			}
			else if(input.equals("SETCORRESPONDENT")){
				String partner = inFromUser.readLine();
				outToServer.writeChars("SETCORRESPONDENT");
				outToServer.writeChars(partner);
				String response = inFromServer.readLine();
				if(response.equals("CORRESPONDENT SUCCESSFULLY SET")){
					correspondent= partner;
					correspondentSet=true;
				}
				else System.out.println(response);
				if(correspondentSet){
					System.out.println("You are currently chatting with: "+ correspondent);
				}
				else{
					System.out.println("You currently aren't chatting with anyone");
				}
			}
			else{ //assumed to be a message
				if(correspondentSet){
					StringBuilder builder = new StringBuilder();
					while(!input.equals("ESCAPE")){
						builder.append(input).append("\n");
						input = inFromUser.readLine();
					}
					builder.append("ESCAPE");
					outToServer.writeChars("SENDMSG:\n"+ correspondent + "/n" + builder.toString());
				}
				else{
					System.out.println("CANNOT SEND MESSAGE BECAUSE CORRESPONDENT NOT SET");
				}
			}
		}
	}
}
