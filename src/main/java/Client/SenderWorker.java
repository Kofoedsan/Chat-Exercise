package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class SenderWorker implements Runnable {
    DataOutputStream out = null;

    public SenderWorker(DataOutputStream out) {
        this.out = out;
    }

    String line;

    @Override
    public void run() {
        System.out.println("testoutput");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                out.writeUTF(scanner.nextLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}