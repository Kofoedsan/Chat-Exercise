package server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClientHandler implements Runnable {
    //    hvorfor static?
    static Vector<String> activeclients = new Vector<>();

//    kunne det ikke være smart hvis denne variabel holdt clienthandler objekter? som Vector<Client>
//    kunne det lette overskueligheden med en klientklasse, så man havde socket-håndtering og User adskilt, ligesom i mario med dbmapper?
    static Vector<String> registredClients = new Vector<>();
    static Vector<Socket> socketList = new Vector<>();
    static Vector<ClientHandler> handler = new Vector<>();
    Socket clientSocket;
    DataInputStream in;
    DataOutputStream out;
    String input = "";
    String command = "";
//    forslag - at ændre username til at være det givne username som serveren håndteer enten det er modtager af besked eller loginforsøg.
    String username = "";
    String message = "";
    String loggedInUser = "";

    boolean isLoggedIn=false;

    //    den endnu ikke #connectede () klient (som kun lige har bundet an til en socket)
    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        //socketList.add(this.clientSocket);
    }

    //    den connectede klient. (hvad sker der med det ikke-connectede objekt når man opretter dette objekt?)
    public ClientHandler(Socket clientSocket, DataInputStream in, DataOutputStream out, String loggedInUser) {
        this.clientSocket = clientSocket;
        this.in = in;
        this.out = out;
        this.loggedInUser = loggedInUser;
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

            while (true) {
                if (in.available() > 1) {
                    input = in.readUTF();
                    StringTokenizer st = new StringTokenizer(input, "#");
                    String username = "";
                    int countTokens = st.countTokens();
                    if (countTokens == 1) {
                        command = st.nextToken();
                    }
                    if (countTokens == 2) {
                        command = st.nextToken();
                        username = st.nextToken();
                    }
                    if (countTokens == 3) {
                        command = st.nextToken();
                        username = st.nextToken();
                        message = st.nextToken();
                    }
                    // NONO  {out.writeUTF("Du har ikke indtastet en gyldig kommand");}

                    switch (command) {
                        case "CONNECT":
                            if (registredClients.contains(username) && !activeclients.contains(username) && this.loggedInUser.isEmpty()) {
                                this.loggedInUser = username;
                                this.isLoggedIn=true;
                                activeclients.add(loggedInUser);


//                man kunne sætte den resterende værdi username, så man beholdt socket. man har vistnok al data udfyldt for newClient, så man kunne:
                                handler.add(this);

//                                ClientHandler newClient = new ClientHandler(clientSocket, in, out, username);
//                                handler.add(newClient);

                                broadcastUsers(onlineCommand());
                            } else out.writeUTF("illegal input was recieved"); //clientSocket.close(); System.exit(1);
                            break;
                        case "SEND":
                            sendMessage(username);
                            break;
                        case "CLOSE":
                            break;
                        // case "4": break;
                        // case "5": break;
                        default:
                            out.writeUTF("Du har ikke indtastet en gyldig kommando");
                            break;
                    }
                }

            }

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

    //Kan erstattes ved at løbe igennem for boolean isLoggedIn evt. TODO
    public StringBuilder onlineCommand() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String activeclient : activeclients) {
            sb.append(activeclient + ", ");
        }
        return sb;
    }

    public void broadcastUsers(StringBuilder sb) throws IOException {
        for (ClientHandler ch : handler) {
            ch.out.writeUTF("ONLINE#" + sb);
        }

    }

    public void sendMessage(String reciver) throws IOException {
        for (ClientHandler ch : handler) {
            if (ch.username.equals(reciver) && ch.isLoggedIn == true) {
                ch.out.writeUTF(this.username + ":" + message);
            }
        }
    }
}



