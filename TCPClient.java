import java.io.*; 
import java.net.*; 
class TCPClient { 

public static void main(String argv[]) throws Exception 
    { 
        String userinput; 
        String response = "UNSUCCESSFUL ATTEMPT TO SET USERNAME"; 

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

        //Prompt user for hostname and port of server they wish to connect with
        System.out.println("Please enter a hostname");
        String hostname = inFromUser.readLine();

        System.out.println("Please enter a port number");
        int port = Integer.parseInt(inFromUser.readLine());

        //establish connection to desired server
        Socket clientSocket = new Socket(hostname, port); 

        //Create output to server
        DataOutputStream outToServer =  new DataOutputStream(clientSocket.getOutputStream());

        //Create input from server
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
        while(response.equals("UNSUCCESSFUL ATTEMPT TO SET USERNAME")){ //Until successful user name is set keep trying
            System.out.println("You will need to set up a username to do anything expecpt end the connection\n");
            System.out.println("Please enter your desired username or ENDCONNECTION to exit\n");
            userinput = inFromUser.readLine(); 

            if(userinput.equals("ENDCONNECTION")){
                System.out.println("Your connection to the server is terminated");
                clientSocket.close();
            }
            else{ //the user is attempting to save a username;
            outToServer.writeBytes(userinput + '\n'); 
           
            }
            response= inFromServer.readLine();  
            System.out.println("FROM SERVER: " + response); 
        }

        boolean applicationOn=true;
        /*Give user options*/
        System.out.println("\n\nThe control messages are: \n'ENCONNECTION' to end your session with the server\n\n"+
            "'SETUPCHAT' to start a chat with another client\n\n'SETUSERNAME' to change your username"+
            "\n\n Anything else will be considered a message for your chat and new messages cannot start with ESCAPE\n\n To end a messsage on a new line write 'ESCAPE'\n\n"); 
        
        while(applicationOn){ /*While the connection is not ended recieve commands*/
            System.out.println("Enter a control messsage or a message to your chat partner");
            String controlMSG = inFromUser.readLine();

            if(controlMSG.equals("ENDCONNECTION")){ //close connection with server
                outToServer.writeBytes("ENDCONNECTION"+'\n');
                System.out.println("Your connection to the server is terminated");
                clientSocket.close();
                applicationOn=false;
            }

            else if(controlMSG.equals("SETUSERNAME")){ //change username
                outToServer.writeBytes("SETUSERNAME" + '\n');
                response ="UNSUCCESSFUL ATTEMPT TO SET USERNAME"; 
                while(response.equals("UNSUCCESSFUL ATTEMPT TO SET USERNAME")){
                    System.out.println("Please enter your desired username \n");
                    userinput = inFromUser.readLine(); 
                    outToServer.writeBytes(userinput + '\n'); 
                    response= inFromServer.readLine();  
                    System.out.println("FROM SERVER: " + response); 
            }
        }

        else if(controlMSG.equals("CHECK MSGS")){ /*Check to see if you have any msgs*/
            if(inFromServer.readLine().equals("RECIEVE MSG")){
                String line = inFromServer.readLine();
                while(!line.equals("ESCAPE")){
                    System.out.println(line);
                    line= inFromServer.readLine();
                }
            }
        }
        
        else if(controlMSG.equals("SETUPCHAT")){ //establish connection with someone
            outToServer.writeBytes("SETUPCHAT" + '\n');
            System.out.println("Please enter the name of the person you'd like to chat with");
            userinput = inFromUser.readLine();
            outToServer.writeBytes(userinput + '\n');
            String output = inFromServer.readLine();
            System.out.println(output);
        }

        else{ //It will be considered a message
            System.out.println("gets to message");
            outToServer.writeBytes(controlMSG+ "\n");
            while(!controlMSG.equals("ESCAPE")){
                controlMSG= inFromUser.readLine();
                outToServer.writeBytes(controlMSG+ "\n");
            }
            outToServer.writeBytes("ESCAPE"+ "\n");
            response = inFromServer.readLine();
            System.out.println(response);
        }

    }
}

       

} 