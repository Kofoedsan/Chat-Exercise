package Client3;

import java.io.DataInputStream;

public class ReaderWorker implements Runnable {
    DataInputStream in = null;
    String incomingText;
    public ReaderWorker(DataInputStream in) {
        this.in = in;
    }
    String line;

    @Override
    public void run() {

        while (true) {
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