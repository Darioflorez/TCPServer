package server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Dario on 2015-09-08.
 */
public class Server {
    public static void main(String args[]){
        try{
            ArrayList<Client> connectedClients = new ArrayList<>();
            int serverPort = 7896;
            ServerSocket listeningSocket = new ServerSocket(serverPort);

            waitingForConnection(listeningSocket, connectedClients);

        }catch(IOException e){
            System.out.println("Listen socket: " + e.getMessage());
        }
    }

    public static void waitingForConnection(ServerSocket listeningSocket,
                                            ArrayList<Client> connectedClients)throws IOException{
        while(true){
            System.out.println("Waiting....");
            
            Socket clientSocket = listeningSocket.accept();
            //Try to create a client if you cannot then throw an exception
            Client client = new Client(clientSocket);
            //Fix if you ca not create a client then do not add the client to the list
            connectedClients.add(client);

            Connection connection = new Connection(client, connectedClients);
            connection.start();
        }
    }
}
