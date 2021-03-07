package server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClientHandler implements Runnable {
    static Vector<String> activeclients = new Vector<>();
    static Vector<String> registredClients = new Vector<>();
    static Vector<Socket> socketList = new Vector<>();
    static Vector<ClientHandler> handler = new Vector<>();
    Socket clientSocket;
    DataInputStream in;
    DataOutputStream out;
    String input ="";
    String command ="";
    String username="";
    String message="";
    String thisUser="";
    boolean isLoggedIn;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        //socketList.add(this.clientSocket);
    }

    public ClientHandler(Socket clientSocket, DataInputStream in, DataOutputStream out, String username) {
        this.clientSocket = clientSocket;
        this.in = in;
        this.out = out;
        this.username = username;
        this.isLoggedIn = true;
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
                case "CONNECT":
                    if (registredClients.contains(username) && !activeclients.contains(username))
                {activeclients.add(username);
                ClientHandler newClient = new ClientHandler(clientSocket, in, out, username);
                handler.add(newClient);
                broadcastUsers(onlineCommand());}
                     else out.writeUTF("illegal input was recieved"); //clientSocket.close(); System.exit(1);
                     break;
                case "SEND":  sendMessage(username); break;
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

    //Kan erstattes ved at løbe igennem for boolean isLoggedIn evt. TODO
    public StringBuilder onlineCommand() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String activeclient : activeclients) {
          sb.append(activeclient+", ");
        }
        return sb;
    }

    public void broadcastUsers(StringBuilder sb) throws IOException {
        for (ClientHandler ch : handler) {
            ch.out.writeUTF("ONLINE#"+sb);
        }

    }

    public void sendMessage(String reciver) throws IOException {
        for (ClientHandler ch : handler) {
         if (ch.username.equals(reciver) && ch.isLoggedIn==true){
              ch.out.writeUTF(this.username+":"+ message);
         }
        }
    }
}



