import java.io.*; 
import java.net.*; 
class TCPClient { 

public static void main(String argv[]) throws Exception 
    { 
        String userinput; 
        String response = "UNSUCCESSFUL ATTEMPT TO SET USERNAME"; 

        BufferedReader inFromUser = 
          new BufferedReader(new InputStreamReader(System.in)); 

        Socket clientSocket = new Socket("o405-u14", 5037); //make sure to go back and prompt for host and port

        DataOutputStream outToServer = 
          new DataOutputStream(clientSocket.getOutputStream());

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
        while(response.equals("UNSUCCESSFUL ATTEMPT TO SET USERNAME")){
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
        System.out.println("\n\nThe control messages are: \n'ENCONNECTION' to end your session with the server\n\n"+
            "'SETUPCHAT' to start a chat with another client\n\n'SETUSERNAME' to change your username"+
            "\n\n Anything else will be considered a message for your chat \n\n To end a messsage on a new line write 'ESCAPE'\n\n"); 
        while(applicationOn){
        String controlMSG = inFromUser.readLine();
        if(controlMSG.equals("ENDCONNECTION")){
            outToServer.writeBytes("ENDCONNECTION"+'\n');
            System.out.println("Your connection to the server is terminated");
            clientSocket.close();
            applicationOn=false;
        }
        else if(controlMSG.equals("SETUSERNAME")){
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
        else if(controlMSG.equals("SETUPCHAT")){
            outToServer.writeBytes("SETUPCHAT" + '\n');
            System.out.println("Please enter the name of the person you'd like to chat with");
            userinput = inFromUser.readLine();
            outToServer.writeBytes(userinput + '\n');
            String output = inFromServer.readLine();
            System.out.println(output);
        }

    }
    }

       

        //outToServer.writeBytes(userinput + '\n'); 

        //response = inFromServer.readLine(); 

        //System.out.println("FROM SERVER: " + response); 

        //clientSocket.close();                    
    } 