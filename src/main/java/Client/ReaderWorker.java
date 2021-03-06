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
//                            System.out.println("Logged out without error");
                            System.out.println("CLOSE#0");
                            in.close();
                            System.exit(1);
                            break;
                        case "CLOSE#1":
//                            System.out.println("Logged out! illegal input was recived");
                            System.out.println("CLOSE#1");
                            in.close();
                            System.exit(1);
                            break;
                        case "CLOSE#2":
//                            System.out.println("Logged out! user not found");
                            System.out.println("CLOSE#2");
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