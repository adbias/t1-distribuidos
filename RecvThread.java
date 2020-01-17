import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.InetAddress;
import java.net.DatagramPacket;

class RecvThread extends Multicast {
    String ip;
    Boolean s;
    InetAddress server;
    int port;
    int client;
    String[][] data;
    RecvThread(String ip, Boolean s, int port, int index) {
        super(port);
        try {
            this.server = InetAddress.getByName(ip);
        } catch (Exception e) {
            e.printStackTrace();
            try {throw new Exception("uwu");} catch (Exception o) {}
        }
        this.s = s;
        this.port = port;
        this.data = new String[1][0];
        this.client = index;
    }

    public void run() {
        try {
            MulticastSocket f = socket_open(this.server);
            // CCast_Queue_Client1_djwjdwj_jdwjdwj_dwdjwjdw_dwjdwj_ID:1919
            Pattern p = Pattern.compile("(Client(\\d+)|CCast)_(\\w+)(_(.+)|)_ID:(\\d+)");
            while (this.s) {
                byte[] buf = new byte[1000];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                f.receive(recv);
                String o = new String(buf, 0, buf.length);
                Matcher m = p.matcher(o);
                if (m.find()) {
                    if (m.group(1).equals("CCast")) {
                        String[] queue = m.group(3).split("_");
                        if (queue[0].equals("Queue")){
                            if (queue[1].equals(String.format("%d", client))) {
                                for (int i =2;i<queue.length;i++) System.out.println(queue[i]);
                            }
                        } else {
                            System.out.println(m.group());
                        }
                    } else {
                        int y = Integer.parseInt(m.group(2));
                        if (this.data.length < y) this.data = Arrays.copyOf(this.data, y);
                        int x = Integer.parseInt(m.group(6));
                        if (this.data[y-1] == null) this.data[y-1] = new String[1];
                        if (this.data[y-1].length < x) this.data[y-1] = Arrays.copyOf(this.data[y-1], x);
                        this.data[y-1][x-1] = m.group();
                    }
                }
            }
            socket_close(f, this.server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String info) {
        try {
            //System.out.println(info);
            MulticastSocket f = socket_open(this.server);
            DatagramPacket msg = new DatagramPacket(info.getBytes(), info.length(), this.server, this.port);
            f.send(msg);
            socket_close(f, this.server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}