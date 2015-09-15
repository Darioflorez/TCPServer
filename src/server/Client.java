package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Dario on 2015-09-14.
 */
public class Client {

    private Socket socket;
    private String address;
    private String nickname;
    private int port;
    private DataInputStream in;
    private DataOutputStream out;


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

    /*public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }*/

    public void createInput() throws IOException{
        in = new DataInputStream(socket.getInputStream());
    }

    public void createOutput()throws IOException{
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void write(String msg)throws IOException{
        out.writeUTF(msg);
    }

    public String read()throws IOException{
        String msg = in.readUTF();
        return msg;
    }

    public void close()throws IOException{
        //close socket
        socket.close();
    }


}
