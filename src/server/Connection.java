package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Dario on 2015-09-08.
 */
public class Connection extends Thread{
    private DataInputStream in;
    private DataOutputStream out;
    private Socket clientSocket;
    private volatile boolean clientActive;
    //private Client client;

    public Connection (Socket _clientSocket){
        try{
            clientSocket = _clientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            clientActive = true;
        }catch(IOException e){
            System.out.println("Connection" + e.getMessage());
        }
    }
    public void run(){
        //While Client Active
        String data;
        while(clientActive){
            System.out.println("Client Connected!!");
            try{
                //Listen for new messages
                while ((data = in.readUTF()) != null) {
                    System.out.println("> " + data);
                    //send back to the client
                    out.writeUTF(data);
                }
            }catch(IOException e){
                System.out.println("readLine " + e.getMessage());
                closeConnection();
            }finally {
                try{
                    clientSocket.close();
                }catch(IOException e){
                    System.out.println("Close connection" + e.getMessage());
                }
            }
        }
    }

    public void closeConnection(){
        clientActive = false;
        System.out.println("Client bye!!");
    }


}
