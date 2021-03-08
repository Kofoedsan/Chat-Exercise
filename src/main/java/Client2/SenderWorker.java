package Client2;

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
//        System.out.println("test outputthread");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
//                System.out.print("klient skriver nu: ");
                out.writeUTF(scanner.nextLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}