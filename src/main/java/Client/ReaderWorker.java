package Client;

import java.io.DataInputStream;
import java.io.IOException;

public class ReaderWorker implements Runnable {
    DataInputStream in;
    String incomingText;
    public ReaderWorker(DataInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {

        while (true) {
            try {
                if(in.available() > 1){
                    incomingText = in.readUTF();
                    System.out.println("besked fra server: " + incomingText);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}