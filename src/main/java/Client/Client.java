package Client;

import com.google.gson.internal.bind.util.ISO8601Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



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
        String ip;
        int port;
        try {
            if (args.length == 2) {
                ip = args[0];
                port = Integer.parseInt(args[1]);
            }
            else {
                throw new IllegalArgumentException("# Server not provided with the right arguments");
            }
        } catch (NumberFormatException ne) {
            System.out.println("# Illegal inputs provided when starting the server!");
            return;
        }
        Thread chatClientThread = new Thread(new Client(ip, port));
        chatClientThread.start();

    }
}