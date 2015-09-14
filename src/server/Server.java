package server;

import java.net.*;
import java.io.*;
/**
 * Created by Dario on 2015-09-08.
 */
public class Server {
    public static void main(String args[]){
        try{
            int serverPort = 7896;
            ServerSocket listeningSocket = new ServerSocket(serverPort);
            waitingForConnection(listeningSocket);
        }catch(IOException e){
            System.out.println("Listen socket: " + e.getMessage());
        }
    }

    public static void waitingForConnection(ServerSocket listeningSocket)throws IOException{
        while(true){
            System.out.println("Waiting....");
            Socket clientSocket = listeningSocket.accept();
            Connection connection = new Connection(clientSocket);
            connection.start();
        }
    }
}
