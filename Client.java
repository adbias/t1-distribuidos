import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    // Made a exit command just in case, a bit useless if you use Ctrl+C
    volatile static boolean info = true;
    public static void main(String[] args) {
        int clientIndex = Integer.parseInt(args[1]);
        String ip = args[0];
        int port = 5000;
        int id = 1;
        RecvThread recvThread = new RecvThread(ip, info, port, clientIndex);
        recvThread.setDaemon(true);
        recvThread.start();
        String base = "Client%d_%s_ID:%d";
        // Pattern so we can match our input
        Pattern p = Pattern.compile("(Play|Stop|Pause|Queue|Next|Jump|History)( \"(.+)\" (\\d+)| (\\d+)|)");
        while (info) {
            //System.out.println(recvThread.get());
            Scanner input = new Scanner(System.in);
            String o = input.nextLine();
            Matcher m = p.matcher(o);
            if (m.find()) {
                if (m.group(1).equals("exit")) {
                    info = false;
                } else if (m.group(1).equals("Stop")) {
                    // Stop playlist
                    recvThread.send(String.format(base, clientIndex, "Stop", id));
                } else if (m.group(1).equals("Play")) {
                    // Command must have the following syntax: play "song name with symbols" duration
                    //'^play "(.+)" (\\d+)$'
                    recvThread.send(String.format(base, clientIndex, String.format("Play_%s_%s", m.group(3), m.group(4)), id));
                } else if (m.group(1).equals("Pause")) {
                    // Pause playlist
                    recvThread.send(String.format(base, clientIndex, "Pause", id));
                } else if (m.group(1).equals("Queue")) {
                    //System.out.println(m.groupCount());
                    // Put something in the queue or display the queue
                    // Command should have the following syntax: queue "song name with symbols" duration
                    if (!m.group(2).equals("")) {
                        String play = String.format("Queue_%s_%s", m.group(3), m.group(4));
                        recvThread.send(String.format(base, clientIndex, play, id));
                    } else {
                        System.out.println("***\tQueue\t***");
                        recvThread.send(String.format(base,clientIndex, "Queue", id));
                    }
                } else if (m.group(1).equals("Next")) {
                    // Play next song in the playlist
                    recvThread.send(String.format(base, clientIndex, "Next", id));
                } else if (m.group(1).equals("Jump")) {
                    // Jump to a forward position
                    recvThread.send(String.format(base, clientIndex, "Jump_"+m.group(5), id));
                } else if (m.group(1).equals("History")) {
                    // Display history
                    recvThread.send(String.format(base, clientIndex, "History", id));
                    System.out.println("***\tHistory\t***");
                    System.out.println(Arrays.deepToString(recvThread.data));
                } else {
                    System.out.println("Comando no encontrado.");
                }
                id++;
            } else {
                System.out.println("Comando no encontrado.");
            }
        }
    }
}