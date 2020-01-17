import java.lang.Thread;
import java.util.concurrent.atomic.AtomicInteger;

// class for our queue
class Ti {
    volatile AtomicInteger progress = new AtomicInteger(1);
    String name = "";
    int duration = 0;
    Ti(String name, int duration) {
        this.duration = duration;
        this.name = name;
    }
}

public class Server {
    // AtomicInteger lets make counters shared between all threads and classes
    volatile static AtomicInteger id = new AtomicInteger(1);
    public static void main(String[] args) {
        String ip = args[0];
        // Port hardcoded
        int port = 5000;
        // We need 2 threads for our server, we didn't use the main thread though...
        RecvServerThread recvThread = new RecvServerThread(ip, port, id);
        SendServerThread sendThread = new SendServerThread(ip, port, recvThread.getData, recvThread.getPause, id);
        sendThread.setDaemon(true);
        sendThread.start();
        recvThread.setDaemon(true);
        recvThread.start();
        while (true) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {}
        }
    }
}