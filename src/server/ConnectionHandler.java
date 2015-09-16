package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Dario on 2015-09-16.
 */
public class ConnectionHandler extends Thread{

    private ArrayList<Connection> connectionsList = new ArrayList<>();

    public void run(){
        ServerSocket listeningSocket = null;
        try {
            ArrayList<Client> connectedClients = new ArrayList<>();
            int serverPort = 7896;
            listeningSocket = new ServerSocket(serverPort);

            waitingForConnection(listeningSocket, connectedClients);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(listeningSocket != null){
                try {
                    listeningSocket.close();
                } catch (IOException e) {
                    System.err.println("Close ServerSocket: " + e.getMessage());
                }
                if(connectionsList != null){
                    for(Connection con: connectionsList){
                        con.closeConnection();
                    }
                }
            }
        }
    }

    private void waitingForConnection(ServerSocket listeningSocket,
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
            connectionsList.add(connection);
        }
    }

}
