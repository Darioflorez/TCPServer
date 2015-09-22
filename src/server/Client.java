package server;

import java.io.*;
import java.net.Socket;

/**
 * Created by Dario on 2015-09-14.
 */
public class Client {

    private Socket socket;
    private String address;
    private String nickname;
    private int port;

    private BufferedReader input;
    private PrintWriter output;


    public Client(Socket _socket)throws IOException{
        this.socket = _socket;
        this.address = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        setNickname(address);
        createInput();
        createOutput();
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public void createInput() throws IOException{
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void createOutput()throws IOException{
        output = new PrintWriter(socket.getOutputStream(),true);
    }

    public void write(String msg){
        output.println(msg);
    }

    public String read()throws IOException {
        return input.readLine();
    }

    public void close()throws IOException{
        //close socket
        socket.close();
        input.close();
        output.close();
    }


}
