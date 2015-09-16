package server;

/**
 * Created by Dario on 2015-09-08.
 */
public class Server {
    public static void main(String args[]){

        ConnectionHandler runner = new ConnectionHandler();
        runner.start();
    }
}
