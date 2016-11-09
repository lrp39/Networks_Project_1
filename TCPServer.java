import java.io.*; 
import java.net.*; 
import java.io.*;
import java.net.*;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.LinkedList;
class TCPServer { 

  private static final int PORT = 5037;
  //keeps track of active users
  private static HashSet<String> users = new HashSet<String>();

  //keep track of chat connections between clients
  //private static LinkedList<Connection> connections = new LinkedList<Connection>();

  //Keeps track of input/output streams to clients
  //private static LinkedList<Clients> clients = new LinkedList<Clients>();

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
          while(username.equals("")){
          proposedUsername = inFromClient.readLine(); 
          System.out.println("TRY: "+ proposedUsername);
          users.add("leah");
          synchronized (users){
              if(!users.contains(proposedUsername)){
                this.username= proposedUsername;
                users.add(proposedUsername);
                response= "SUCCESSFULLY SET USERNAME";
                System.out.println("New user: "+ this.username);
                System.out.println(response);
                outToClient.writeBytes(response + '\n');
              }
              else{
                response = "UNSUCCESSFUL ATTEMPT TO SET USERNAME";
                System.out.println(response);
                outToClient.writeBytes(response + '\n');
              }
            }
          }
          Boolean applicationOn=true; 
          String controlMSG = inFromClient.readLine();
          while(applicationOn){
            if(controlMSG.equals("ENDCONNECTION")){
                applicationOn=false;
                users.remove(username);
                System.out.println("Connection with user: " + username + " is terminated");
                socket.close();
            }
            else if(controlMSG.equals("SETUSERNAME")){ //left off here letting users change their names

            }
          }

      } 
    catch (IOException e) {
                System.out.println(e);
    }
  } 


  }
}







