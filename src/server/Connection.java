package server;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Dario on 2015-09-08.
 */
public class Connection extends Thread{
    private ArrayList<Client> connectedClients;
    private Client client;
    private volatile boolean clientActive;

    public Connection (Client _client, ArrayList<Client> _connectedClients){
        this.clientActive = true;
        this.connectedClients = _connectedClients;
        this.client = _client;
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
            //this exceptions means that the socket you try to read from is closed
            msg = client.read();
        }catch(IOException e){
            System.out.println("getMessage: " + e.getMessage());
        }
        return msg;
    }

    public void closeConnection(){
        try{
            clientActive = false;
            client.close();
            System.out.println("Client bye!!");
        }catch(IOException e){
            System.out.println("Close connection: " + e.getMessage());
        }
    }

    public boolean respond(String msg){
        msg = client.getNickname() + ": " + msg;
        try {
            client.write(msg);
            return true;
        }catch(IOException e){
            System.out.println("Respond: " + e.getMessage());
            return false;
        }
    }

    /*public synchronized void broadcast(String msg){
        for(Client client: connectedClients){
            //What happens if a client fails?
            client.write(msg);
        }
    }*/

    public String getNick(String msg){
        String[] nickname = msg.split("<");
        nickname = nickname[1].split(">");
        return nickname[0];
    }

    public String whoIsConnected(){
        String listOfClients = null;
        if(connectedClients != null){
            listOfClients = "list of connected clients:" + "\n";
            for(Client client: connectedClients){
                listOfClients += "<-> " + client.getNickname() + "\n";
            }
        }
        return listOfClients;
    }

    public String getAvailableCommands(){
        String space = "               ";
        String commands = "Available commands:" + "\n";
        String help = "/help" + space + ": return a list of all available commands" + "\n";
        String who = "/who" + space + ": return a list of all connected clients" + "\n";
        String nick = "/nick <nickname>" + space + ": set a nick name for this client" + "\n";
        String quit = "/quit" + space + ": disconnect this client" + "\n";
        commands += space + help;
        commands += space + who;
        commands += space + nick;
        commands += space + quit;

        return commands;
    }

    public void handleCommand(String msg){
        String[] cmd = msg.split("/");
        //Make this in a function
        if(cmd[1].contains("nick")){
            String nickname = getNick(cmd[1]);
            client.setNickname(nickname);
            respond(nickname);
        }else{
            switch (cmd[1]){
                case "help":
                    String commands = getAvailableCommands();
                    respond(commands);
                    break;
                case "quit":
                    closeConnection();
                    break;
                case "who":
                    String listOfClients = whoIsConnected();
                    respond(listOfClients);
                    break;
                default:
                    respond("This is not a command!!!");
            }
        }
    }

    public boolean isCommand(String msg){
        return msg.contains("/");
    }

    public void evalMessage(String msg){
        if(isCommand(msg)){
            handleCommand(msg);
        }else{
            //broadcast(msg);
            respond(msg);
        }

    }


}
