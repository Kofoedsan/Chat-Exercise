package server;

import java.io.DataInputStream;
import java.util.Scanner;

public class ReaderWorker implements Runnable {
    DataInputStream in = null;

    public ReaderWorker(DataInputStream in) {
        this.in = in;
    }
    String line;

    @Override
    public void run() {
        String incomingText = "";
        System.out.println("vi er i servers readerworker thread");
        Scanner scanner = new Scanner(System.in);
        while (!incomingText.equals("Daniel")) {
            try {
                incomingText = in.readUTF();
//                    if (!incomingText.equalsIgnoreCase("0")) {
                System.out.println("besked fra klient: " + incomingText);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}