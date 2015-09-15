package server;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Dario on 2015-09-08.
 */

/*Problem list
    What to do when a client close?
        All the other client have to know this
        This client have to be delete from de connectedClients list
    Make a client whit two thread. Listen/Write.
    Try to create a communication module
*
* */

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
        try {
            respond("welcome!");
            while(clientActive){
                msg = getMessage();
                if(msg != null){
                    System.out.println("> " + msg);
                    try {
                        handleMessage(msg);
                    } catch (IOException e) {
                        //Think this exception can be arisen by another client than you
                        System.out.println("Problem when sending message: " + e.getMessage());
                        //close connection if this client throws the exception
                    }
                }
                else{
                    closeConnection();
                }
            }
        } catch (IOException e) {
            //This exception can only be arisen by you
            System.out.println("Problem when sending welcome: " + e.getMessage());
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
            connectedClients.remove(client);
            System.out.println("Client bye!!");
        }catch(IOException e){
            System.out.println("Close connection: " + e.getMessage());
        }
    }

    public void respond(String msg)throws IOException{
        msg = client.getNickname() + ": " + msg;
        client.write(msg);
    }

    public synchronized void broadcast(String msg)throws IOException{
        String from = client.getNickname();
        for(Client client: connectedClients){
            //What happens if a client fails?
            client.write(from + ": " + msg);
        }
    }

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

    public void handleCommand(String msg)throws IOException{
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
                    respond("unknown command!");
            }
        }
    }

    public boolean isCommand(String msg){
        return msg.contains("/");
    }

    public void handleMessage(String msg)throws IOException{
        if(isCommand(msg)){
            handleCommand(msg);
        }else{
            broadcast(msg);
            //respond(msg);
        }

    }


}
