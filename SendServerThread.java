import java.net.MulticastSocket;
import java.net.InetAddress;
import java.lang.Thread;
import java.net.DatagramPacket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

class SendServerThread extends Multicast {
    String ip;
    Boolean s;
    InetAddress server;
    int port;
    AtomicInteger count;
    Function<Void,Boolean> pause;
    Function<Boolean, Ti[]> getData;
    SendServerThread(String ip, int port, Function<Boolean,Ti[]> queue, Function<Void,Boolean> pause, AtomicInteger id) {
        super(port);
        try {
            this.server = InetAddress.getByName(ip);
        } catch (Exception e) {
            e.printStackTrace();
            try {throw new Exception("uwu");} catch (Exception o) {}
        }
        this.port = port;
        this.pause = pause;
        this.count = id;
        this.getData = queue;
    }

    public void run() {
        try {
            String message = "CCast_%s_ID:%d";
            MulticastSocket f = socket_open(this.server);
            while (true) {
                Ti[] queue = this.getData.apply(false);
                if (queue.length > 0 && !this.pause.apply(null)) {
                    System.out.print("\r");
                    if (queue[0].progress.get() > queue[0].duration) {
                        this.getData.apply(true);
                    } else {
                        String info = String.format(message, "Play_" + queue[0].name + "_" + ((queue[0].progress.get()*100/queue[0].duration))+" %", this.count.get());
                        DatagramPacket msg = new DatagramPacket(info.getBytes(), info.length(), this.server, this.port);
                        f.send(msg);
                        this.count.getAndIncrement();
                        queue[0].progress.incrementAndGet();
                        Thread.sleep(1000);
                    }
                } else {
                    Thread.sleep(500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

