package server;

/**
 * Created by Dario on 2015-09-14.
 */
public class Client {
    private String address;
    private String nickname;
    private int port;

    public Client(String _address, int _port){
        this.address = _address;
        this.port = _port;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }


}
