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
        System.out.println("Client Connected!!");
        //While Client Active
        String msg;
        if(respond("welcome")){
            while(clientActive){
                msg = getMessage();
                if(msg != null){
                    System.out.println("> " + msg);
                    evalMessage(msg);
                }
                else{
                    closeConnection();
                }
            }
        }
        else{
            closeConnection();
        }
    }

    protected String getMessage(){
        String msg = null;
        try {
            msg = in.readUTF();
        }catch(IOException e){
            System.out.println("getMessage: " + e.getMessage());
        }
        return msg;
    }

    public void closeConnection(){
        try{
            clientActive = false;
            clientSocket.close();
            System.out.println("Client bye!!");
        }catch(IOException e){
            System.out.println("Close connection: " + e.getMessage());
        }
    }

    public boolean respond(String msg){
        try {
            out.writeUTF(msg);
            return true;
        }catch(IOException e){
            System.out.println("Respond: " + e.getMessage());
            return false;
        }
    }

    public void evalMessage(String msg){
        if(msg.contains("/")){
            String[] cmd = msg.split("/");
            switch (cmd[1]){
                case "help":
                    respond("YOU NEED HELP!");
                    break;
                case "quit":
                    closeConnection();
                    break;
                case "who":
                    respond("WHO!!!");
                    break;
                default:
                    respond("This is not a command!!!");
            }
        }else{
            respond(msg);
        }

    }


}
