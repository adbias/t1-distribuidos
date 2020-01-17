import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Function;

class RecvServerThread extends Multicast {
    String ip;
    Boolean s;
    InetAddress server;
    int port;
    // Queue
    Ti[] data = new Ti[0];
    Boolean pause = false;
    AtomicInteger count;
    RecvServerThread(String ip, int port, AtomicInteger id) {
        super(port);
        try {
            this.server = InetAddress.getByName(ip);
        } catch (Exception e) {
            e.printStackTrace();
            try {throw new Exception("uwu");} catch (Exception o) {}
        }
        this.port = port;
        this.count = id;
    }

    public void run() {
        try {
            MulticastSocket f = socket_open(this.server);
            // Regex, match orders
            Pattern p = Pattern.compile("(Client(\\d+))_(Play|Pause|Stop|Queue|Jump|Next)(_(.+)|)_ID:(\\d+)");
            Pattern p2 = Pattern.compile("^(.+)_(\\d+)$");
            while (true) {
                byte[] buf = new byte[1000];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                f.receive(recv);
                String o = new String(buf, 0, buf.length);
                Matcher m = p.matcher(o);
                if (m.find()) {
                    System.out.println(m.group());
                    if (m.group(3).equals("Play")) {
                        // Empty our queue if it had something and fill it with new entries
                        this.data = new Ti[1];
                        Matcher m2 = p2.matcher(m.group(5));
                        if (m2.find()) {
                            this.data[0] = new Ti(m2.group(1), Integer.parseInt(m2.group(2)));
                            // It should match here
                        } else {
                            System.out.println("Uh oh :c (line 114)");
                        }
                    } else if (m.group(3).equals("Queue")) {
                        if (!m.group(4).equals("")) {
                            // Add data to our queue
                            int x = this.data.length;
                            this.data = Arrays.copyOf(this.data, x + 1);
                            Matcher m2 = p2.matcher(m.group(5));
                            if (m2.find()) {
                                this.data[x] = new Ti(m2.group(1), Integer.parseInt(m2.group(2)));
                                // It should match here
                            }
                        } else {
                            if (this.data.length > 0) {
                                String info = this.data[0].name;
                                for (int i=1;i<this.data.length;i++) info = info+"_"+this.data[i].name;
                                send("Queue_"+m.group(2)+"_"+info);
                            } else {
                                send("Queue_"+m.group(2)+"_"+"Vacio");
                            }
                        }
                    } else if (m.group(3).equals("Pause")) {
                        // Special status, we pause the other thread with this flag
                        if (!this.pause) {
                            send("Pause_(Pause para reanudar)");
                        }
                        this.pause = !this.pause;
                    } else if (m.group(3).equals("Stop")) {
                        // Empty the queue
                        this.data = new Ti[0];
                        send("Stop");
                    } else if (m.group(3).equals("Jump")) {
                        // Position should be between 2 and length - 1
                        // 1 is the same song and length makes the queue empty
                        if (Integer.parseInt(m.group(5)) > 1 && Integer.parseInt(m.group(5))-1 < this.data.length) {
                            this.data = Arrays.copyOfRange(this.data, Integer.parseInt(m.group(5))-1, this.data.length);
                        }
                    } else if (m.group(3).equals("Next")) {
                        getData.apply(true);
                    }
                }   
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Auxiliar send, we don't need to send pause every second!
    public void send(String info) {
        try {
            String message = "CCast_%s_ID:%d";
            String m = String.format(message, info, this.count.get());
            MulticastSocket f = socket_open(this.server);
            DatagramPacket msg = new DatagramPacket(m.getBytes(), m.length(), this.server, this.port);
            f.send(msg);
            socket_close(f, this.server);
            this.count.getAndIncrement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Special functions made for our other thread, we need to ask for values in this thread
    Function<Boolean, Ti[]> getData = (e) -> {
        if (e) {
            this.data = Arrays.copyOfRange(this.data, 1, this.data.length);
        }
        return this.data;
    };
    Function<Void, Boolean> getPause = e -> this.pause;
}