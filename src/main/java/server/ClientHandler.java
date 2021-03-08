package server;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClientHandler implements Runnable {

    static Vector<String> activeclients = new Vector<>();
    static Vector<ClientHandler> handler = new Vector<>();
    //    kunne det ikke være smart hvis denne variabel holdt clienthandler objekter? som Vector<Client>
//    kunne det lette overskueligheden med en klientklasse, så man havde socket-håndtering og User adskilt, ligesom i mario med dbmapper?
    protected Vector<String> registredClients = new Vector<>();
    protected Socket clientSocket;
    protected DataInputStream in;
    protected  DataOutputStream out;
    protected String input = "";
    protected String command = "";
    //    forslag - at ændre username til at være det givne username som serveren håndteer enten det er modtager af besked eller loginforsøg.
   protected String username = "";
    protected  String message = "";
    protected String loggedInUser = "";
    protected boolean isLoggedIn = false;

    //    den endnu ikke #connectede () klient (som kun lige har bundet an til en socket)
    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        //socketList.add(this.clientSocket);
    }

    //    den connectede klient. (hvad sker der med det ikke-connectede objekt når man opretter dette objekt?)
    public ClientHandler(Socket clientSocket, DataInputStream in, DataOutputStream out, String username, boolean isLoggedIn) {
        this.clientSocket = clientSocket;
        this.in = in;
        this.out = out;
        this.username =username;
        this.isLoggedIn = isLoggedIn;
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
                                loggedInUser = username;
                                isLoggedIn = true;
                                activeclients.add(loggedInUser);
                                ClientHandler newClient = new ClientHandler(clientSocket, in, out, username, isLoggedIn);
                                handler.add(newClient);
                               broadcastUsers(onlineCommand());
                            } else out.writeUTF("illegal input was recieved"); //clientSocket.close(); System.exit(1);
                            break;
                        case "SEND": if(username.equals("*")) {sendToaAll();} else {
                            sendMessage(username);}
                            break;
                        case "CLOSE": out.writeUTF("CLOSE#0");
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
        for (ClientHandler ch : handler) {
            sb.append(ch.username +", ");

        }
        return sb;
    }

    public void broadcastUsers(StringBuilder sb) throws IOException {
        for (ClientHandler ch : handler) {
            ch.out.writeUTF("ONLINE#" + sb);
        }

    }

    public void sendToaAll() throws IOException {
        for (ClientHandler ch : handler) {
            ch.out.writeUTF( "MESSAGE#"+loggedInUser+"#"+message);
        }
    }

    public void sendMessage(String username) throws IOException {

        for (ClientHandler ch : handler) {
            if (ch.username.equals(username) && ch.isLoggedIn == true) {
                ch.out.writeUTF("MESSAGE#"+loggedInUser + "#" + message);
            }

        }
    }
}



