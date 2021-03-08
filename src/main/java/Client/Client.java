package Client;

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
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        DataInputStream in = null;
        try {
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataOutputStream out = null;
        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread inputThread = new Thread(new ReaderWorker(in));
        Thread outputThread = new Thread(new SenderWorker(out));
        inputThread.start();
        outputThread.start();
    }

    public static void main(String[] args) {
        Thread chatClientThread = new Thread(new Client("0.0.0.0", 8080));
        chatClientThread.start();
    }
}