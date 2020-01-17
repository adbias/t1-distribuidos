import java.net.MulticastSocket;
import java.net.InetAddress;
import java.lang.Thread;

class Multicast extends Thread {
    int port;
    Multicast(int port) {
        this.port = port;
    }
    public MulticastSocket socket_open(InetAddress server) {
        try {
            MulticastSocket socket = new MulticastSocket(this.port);
            socket.joinGroup(server);
            return socket;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void socket_close(MulticastSocket socket, InetAddress server) {
        try {
            socket.leaveGroup(server);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}