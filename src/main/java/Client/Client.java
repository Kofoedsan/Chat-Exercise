package Client;

import com.google.gson.internal.bind.util.ISO8601Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class Client implements Runnable {
    Socket socket = null;

    public Client(String s, int i) {
        try {
            this.socket = new Socket(s, i);
        } catch (IOException e) {

            System.out.println("Could not connect to server.. Stopping system... ");
            System.exit(1);
        }
    }


    @Override
    public void run() {
        DataInputStream in = null;
        try {
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Could not open connection on selected port.");
        }

        DataOutputStream out = null;
        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Could not open connection on selected port.");
        }
        Thread inputThread = new Thread(new ReaderWorker(in));
        Thread outputThread = new Thread(new SenderWorker(out));
        inputThread.start();
        outputThread.start();
    }

    public static void main(String[] args) {
        Thread chatClientThread = new Thread(new Client("0.0.0.0", 9000));
        chatClientThread.start();

    }
}