package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Scanner;

public class ReaderWorker implements Runnable {
    DataInputStream in = null;

    public ReaderWorker(DataInputStream in) {
        this.in = in;
    }
    String line;

    @Override
    public void run() {
        String incomingText = "1";
        System.out.println("test input");
        Scanner scanner = new Scanner(System.in);
        while (!incomingText.equals("stop")) {
            try {
                incomingText = in.readUTF();
//                    if (!incomingText.equalsIgnoreCase("0")) {
                System.out.println("besked fra server: " + incomingText);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}