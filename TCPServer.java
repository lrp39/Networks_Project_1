import java.io.*;
import java.net.*;

//store the usernames of clients


//alert user if not a listener, not online, busy chatting with someone else
//alert if attempting to connect to someone already talking to
//alert if the user attempts to send a text to the correspondent but they are no longer online
	//or switched to someone else
//notification in corresopondent switches to another user

//use 38+5500= 5538
public class TCPServer { 

public static void main(String argv[]) throws Exception 
    { 
      String clientSentence; 
      String capitalizedSentence; 

      ServerSocket welcomeSocket = new ServerSocket(6789); 
  
      while(true) {   
           Socket connectionSocket = welcomeSocket.accept(); 

           BufferedReader inFromClient = 
              new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

           DataOutputStream  outToClient = 
             new DataOutputStream(connectionSocket.getOutputStream()); 

           clientSentence = inFromClient.readLine(); 
           capitalizedSentence = clientSentence.toUpperCase() + '\n'; 
           outToClient.writeBytes(capitalizedSentence); 



       } 
    } 
}     
