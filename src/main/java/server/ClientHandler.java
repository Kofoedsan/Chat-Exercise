package server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    Socket clientSocket;
    DataInputStream in;
    DataOutputStream out;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
    }


    @Override
    public void run() {
        //TODO: lytteLoop
        //TODO: COMMANDS
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            out.writeUTF("Hello from thread: " + Thread.currentThread().getName());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        try {
            out.writeUTF("Connected");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        Thread inputThread = new Thread(new ReaderWorker(in));
        Thread outputThread = new Thread(new SenderWorker(out));
        inputThread.start();
        outputThread.start();


//        Thread lytteLoop = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String inputString = "";
//                while (true) {
//                    try {
//                        inputString = in.readUTF();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("client " + clientSocket.getInetAddress() + " skriver: " + inputString);
////                }
////            }
////        });
//
//        Thread outputLoop = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String outputString = "";
//                Scanner scanner = new Scanner(System.in);
//                while (true) {
//                    outputString = scanner.nextLine();
//                    try {
//                        out.writeUTF(outputString);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("server skriver: " + outputString);
//                }
//            }
//        });
//


        //TODO: Remember to close streams
    }
}