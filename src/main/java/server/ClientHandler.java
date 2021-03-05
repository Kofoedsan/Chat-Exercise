package server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    Socket clientSocket;
    DataInputStream in;
    DataOutputStream out;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
    }


    @Override
    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            out.writeUTF("Hello from thread: "+Thread.currentThread().getName());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        try {
            out.writeUTF("Connected");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        //TODO: Remember to close streams
    }
}