package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class SenderWorker implements Runnable {
    DataOutputStream out;

    public SenderWorker(DataOutputStream out) {
        this.out = out;
    }

    String line;
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                line = scanner.nextLine();
                out.writeUTF(line);
            } catch (IOException e) {
                System.out.println("Error in sending message to server ");
            }
        }
    }
}