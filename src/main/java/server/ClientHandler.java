package server;


import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClientHandler implements Runnable {

    //    behøver man at have både handler og activeUsers?
    static Vector<String> activeUsers = new Vector<>();
    static Vector<ClientHandler> handler = new Vector<>();
    //    kunne det ikke være smart hvis denne variabel holdt clienthandler objekter? som Vector<Client>
//    kunne det lette overskueligheden med en klientklasse, så man havde socket-håndtering og User adskilt, ligesom i mario med dbmapper?
    protected Vector<String> registeredUsers = new Vector<>();
    protected Socket clientSocket;
    protected DataInputStream in;
    protected DataOutputStream out;
    protected String input = "";
    protected String command = "";
    protected boolean isRunning = true;
    //    forslag - at ændre username til at være det givne username som serveren håndteer enten det er modtager af besked eller loginforsøg.
    protected String username = "";
    protected String message = "";
    protected String loggedInUser = "";
    protected boolean isLoggedIn = false;
    protected Thread runningThread = null;
    private LogHandler logger = new LogHandler();

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
        this.username = username;
        this.isLoggedIn = isLoggedIn;
    }


    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        registeredUsers.add("USER1");
        registeredUsers.add("USER2");
        registeredUsers.add("USER3");


        //TODO: lytteLoop
        //TODO: COMMANDS
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            while (isRunning) {
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
                            if (registeredUsers.contains(username) && this.loggedInUser.isEmpty() && !activeUsers.contains(username)) {
                                loggedInUser = username;
                                isLoggedIn = true;
                                activeUsers.add(loggedInUser);
//                                ClientHandler newClient = new ClientHandler(clientSocket, in, out, username, isLoggedIn);
//                                handler.add(newClient);
                                handler.add(this);
                                broadcastUsers(onlineCommand());
                                logger.addLog("CONNECT: User " + username + " logged in ");
                                for (ClientHandler clientHandler : handler) {
                                    System.out.println(clientHandler.in);
                                    System.out.println(clientHandler.out);
                                }
                            } else {
                                out.writeUTF("username incorrect"); //clientSocket.close(); System.exit(1);
//                                logger.addLog("Illegal input recived for client " + clientSocket + " terminating connection");
                                logger.addLog("CLOSE#2: client with address " + clientSocket.getInetAddress() + " disconnected");
                                close("2");
                            }
                            break;
                        case "SEND":
                            if (username.equals("*")) {
                                sendToaAll();
                            } else {
                                sendMessage(username);
                            }
                            break;
                        case "CLOSE":
//                            logger.addLog("User" + username + " logged out ");
                            logger.addLog("CLOSE#0: User " + username + " logged out ");

                            close("0");
                            break;
                        default:

// kommandoen er ikke lykkedes - hvis der ikke er connected: CLOSE#1.
                            if (isLoggedIn == false) {
                                logger.addLog("CLOSE#1: client with address " + clientSocket.getInetAddress() + "disconnected");
                                close("1");
                            }
                            if (isLoggedIn == true) {
                                out.writeUTF("Du har ikke indtastet en gyldig kommando");
                            }
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void close(String closeType) {
        try {
            switch (closeType) {
//               normal close
                case "0":
                    Iterator<String> i2 = activeUsers.iterator();
                    while (i2.hasNext()) {
                        String ac = i2.next();
                        if (ac.equals(loggedInUser)) {
                            i2.remove();
                        }
                    }
                    Iterator<ClientHandler> i1 = handler.iterator();
                    while (i1.hasNext()) {
                        ClientHandler ch = i1.next();
                        if (ch.username.equals(loggedInUser)) {
                            i1.remove();
                        }
                    }

                    broadcastUsers(onlineCommand());
//                    out.writeUTF("Illegal input recived." + "\n" + "terminating connection");
                    out.writeUTF("CLOSE#" + closeType);
                    isLoggedIn = false;
                  //  out.close();
                   // in.close(); pool lukker selv, kan skabe fejl for andre users MÅSKE -> THIS. ??
                   // clientSocket.close();
                    isRunning = false;
                    break;

//                    illigal input - (client not: logged in, added to activeUsers + handler)
                case "1":
                case "2":
//                    out.writeUTF("Illegal input recived." + "\n" + "terminating connection");
                    out.writeUTF("CLOSE#" + closeType);
//                  isLoggedIn=false;
//                  out.close();   pool lukker selv, kan skabe fejl for andre users MÅSKE ->THIS. ??
//                  in.close();
//                  clientSocket.close();
                    isRunning = false;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        out.writeUTF("CLOSE#" + closeType);
//
//            //  activeclients.remove(loggedInUser);
//            // loggedInUser ="";
//            //isRunning = false;
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
//        try {
//            broadcastUsers(onlineCommand());
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }

    }


    //Kan erstattes ved at løbe igennem for boolean isLoggedIn evt. TODO
    public StringBuilder onlineCommand() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (ClientHandler ch : handler) {
            sb.append(ch.username + ", ");
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
            ch.out.writeUTF("MESSAGE#" + loggedInUser + "#" + message);
        }
    }

    public void sendMessage(String username) throws IOException {

        for (ClientHandler ch : handler) {
            if (ch.username.equals(username) && ch.isLoggedIn == true) {
                ch.out.writeUTF("MESSAGE#" + loggedInUser + "#" + message);
            }
        }
    }
}



