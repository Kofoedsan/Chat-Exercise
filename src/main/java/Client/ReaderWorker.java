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
                incomingText = in.readUTF();
                if(incomingText.contains("CLOSE#")){
                    switch (incomingText) {
                        case "CLOSE#0":
                            System.out.println("Logged out without error");
                            in.close();
                            System.exit(1);
                            break;
                        case "CLOSE#1":
                            System.out.println("Logged out! illegal input was recived");
                            in.close();
                            System.exit(1);
                            break;
                        case "CLOSE#2":
                            System.out.println("Logged out! user not found");
                            in.close();
                            System.exit(1);
                            break;
                    }}else{
                        System.out.println(incomingText);}
            } catch (IOException ioException) {
                System.out.println("Connection lost ");
                System.exit(1);
        }


        }
                }
            }