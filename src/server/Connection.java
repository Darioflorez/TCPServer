package server;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by Dario on 2015-09-08.
 */

/*Problem list
    What to do when a client close?
        All the other client have to know this
        This client have to be delete from de connectedClients list (this can be seen as a group)
    Make a client whit two thread. Listen/Write.
    Try to create a communication module (Group communication style)
        every time a new client connect to the server it is added to the group
        Server send the message to this group. Ex aGroup.send(aMessage);
    A member can leave(destroy process), fail(destroy process), join(create process)
        notify all members when a process is added or excluded
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
        respond("welcome!");
        while(clientActive){
            try {
                msg = getMessage();
                if(msg != null){
                    System.out.println(client.getNickname() + ": " + msg);
                    handleMessage(msg);
                }else {
                    closeConnection();
                }
            }catch(SocketException e){
                System.err.println("Client error!");
                //finish thread
            } catch (IOException e) {
                System.out.println("Client died!");
                e.printStackTrace();
            }
        }
        System.out.println("Thread exit!");
    }

    protected String getMessage()throws IOException{
        return client.read();
    }

    public void closeConnection(){
        try{
            clientActive = false;
            client.close();
            connectedClients.remove(client);
            broadcast(client.getNickname() + " exits!");
            System.out.println("Client bye!!");
        }catch(IOException e){
            System.out.println("Close connection: " + e.getMessage());
        }
    }

    public void respond(String msg){
        msg = client.getNickname() + ": " + msg;
        client.write(msg);
    }

    public synchronized void broadcast(String msg){
        String from = client.getNickname();
        for(Client client: connectedClients){
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
            listOfClients = "list of connected clients:";
            for(Client client: connectedClients){
                listOfClients +=  "<#>" + "<-> " + client.getNickname();
            }
        }
        return listOfClients;
    }

    public String getAvailableCommands(){
        String space = "               ";
        String commands = "Available commands:" + "<#>";
        String help = "/help" + space + ": return a list of all available commands" + "<#>";
        String who = "/who" + space + ": return a list of all connected clients" + "<#>";
        String nick = "/nick <nickname>" + space + ": set a nick name for this client" + "<#>";
        String quit = "/quit" + space + ": disconnect this client" + "<#>";
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
            //nick name have to be unique
            String nickname = getNick(cmd[1]);
            if(nicknameIsUsed(nickname)){
                respond("This nick name is not available!");
            } else{
                client.setNickname(nickname);
                respond(nickname);
            }
        }else{
            switch (cmd[1]){
                case "help":
                    String commands = getAvailableCommands();
                    respond(commands);
                    break;
                case "quit":
                    //member leave
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

    protected boolean nicknameIsUsed(String nickname){
        for(Client client: connectedClients){
            if(client.getNickname().equalsIgnoreCase(nickname)){
                return true;
            }
        }
        return false;
    }

    public boolean isCommand(String msg){
        return msg.contains("/");
    }

    public void handleMessage(String msg)throws IOException{
        if(isCommand(msg)){
            handleCommand(msg);
        }else{
            broadcast(msg);
        }

    }


}
