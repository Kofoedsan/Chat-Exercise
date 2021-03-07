package server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClientHandler implements Runnable {
    static Vector<String> activeclients = new Vector<>();
    static Vector<String> registredClients = new Vector<>();
    Socket clientSocket;
    DataInputStream in;
    DataOutputStream out;
    String input ="";
    String command ="";
    String username="";
    String message="";
    String thisUser="";

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
    }


    @Override
    public void run() {
        registredClients.add("user1");
        registredClients.add("user2");
        registredClients.add("user3");


        //TODO: lytteLoop
        //TODO: COMMANDS
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            out.writeUTF("Hello from thread: " + Thread.currentThread().getName());
            while(true){
            if(in.available() > 1){
                input = in.readUTF();
                StringTokenizer st = new StringTokenizer(input, "#");
                int countTokens = st.countTokens();
                if (countTokens == 1){command = st.nextToken();}
                if (countTokens == 2){command = st.nextToken(); username = st.nextToken();}
                if (countTokens == 3){command = st.nextToken(); username = st.nextToken(); message = st.nextToken();}
               // NONO  {out.writeUTF("Du har ikke indtastet en gyldig kommand");}

            switch (command){
                case "CONNECT": if (registredClients.contains(username) && !activeclients.contains(username))
                {activeclients.add(username); onlineCommand();}
                else out.writeUTF("illegal input was recieved"); //TODO: SHUTDOWN!!!
                     break;
                case "SEND": break;
                case "CLOSE": break;
               // case "4": break;
               // case "5": break;
                default: out.writeUTF("Du har ikke indtastet en gyldig kommando"); break;
            }}

            }

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
    public void onlineCommand() throws IOException {
        for (String activeclient : activeclients) {
            out.writeUTF("ONLINE#" + activeclient);
        }
    }
}