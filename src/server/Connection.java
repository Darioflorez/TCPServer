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

    public Connection (Socket _clientSocket){
        try{
            clientSocket = _clientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        }catch(IOException e){
            System.out.println("Connection" + e.getMessage());
        }
    }
    public void run(){
        try{
            String data = in.readUTF();
            out.writeUTF(data);
        }catch(IOException e){
            System.out.println("readLine" + e.getMessage());
        }finally {
            try{
                clientSocket.close();
            }catch(IOException e){
                System.out.println("Close connection" + e.getMessage());
            }
        }
    }


}
