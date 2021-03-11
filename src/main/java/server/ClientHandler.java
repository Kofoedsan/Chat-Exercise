package server;


import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClientHandler implements Runnable {

    static Vector<String> activeUsers = new Vector<>();
    static Vector<ClientHandler> handler = new Vector<>();
    protected Vector<String> registeredUsers = new Vector<>();
    protected Socket clientSocket;
    protected DataInputStream in;
    protected DataOutputStream out;
    protected String input = "";
    protected String command = "";
    protected boolean isRunning = true;
    protected String username = "";
    protected String message = "";
    protected String loggedInUser = "";
    protected boolean isLoggedIn = false;
    protected Thread runningThread = null;
    private LogHandler logger = new LogHandler();


    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
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
                    if (countTokens <= 0 || countTokens > 3) {
                        close("1");
                    }
                    if (countTokens == 1) {
                        command = st.nextToken();
                        if (!command.equalsIgnoreCase("CLOSE")) {
                            close("1");
                        }
                    }
                    if (countTokens == 2) {
                        command = st.nextToken();
                        username = st.nextToken();
                        if (!command.equalsIgnoreCase("CONNECT")) {
                            close("1");
                        }
                    }
                    if (countTokens == 3) {
                        command = st.nextToken();
                        if (command == "CLOSE") close("3");
                        username = st.nextToken();
                        if (!registeredUsers.contains(username)) {
                            close("3");
                        }
                        message = st.nextToken();
                    }
                    // NONO  {out.writeUTF("Du har ikke indtastet en gyldig kommand");}

                    switch (command) {
                        case "CONNECT":
                            if (registeredUsers.contains(username) && this.loggedInUser.isEmpty() && !activeUsers.contains(username)) {
                                loggedInUser = username;
                                isLoggedIn = true;
                                activeUsers.add(loggedInUser);
//
                                handler.add(this);
                                broadcastUsers(onlineCommand());
                                logger.addLog("CONNECT: User " + username + " logged in ");
                            } else {
                                logger.addLog("CLOSE#2: client with address " + clientSocket.getInetAddress() + " disconnected");
                                close("2");
                            }
                            break;
                        case "SEND":
                            if (username.equals("*")) {
                                sendToaAll();
                            }
                                if (username.contains(",")) {
                                    sendToaSelected(username);
                                }
                                if (username.equals(username)) {
                                    sendMessage(username);
                                }
                            break;
                        case "CLOSE":
                            logger.addLog("CLOSE#0: User " + loggedInUser + " logged out ");
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
                    out.writeUTF("CLOSE#" + closeType);
                    this.isLoggedIn = false;
                    this.isRunning = false;
                    break;

//                    illigal input - (client not: logged in, added to activeUsers + handler)
                case "1":
                    out.writeUTF("CLOSE#" + closeType);
                    this.isLoggedIn = false;
                    this.isRunning = false;
                    break;
                case "2":
                    out.writeUTF("CLOSE#" + closeType);
                    this.isLoggedIn = false;
                    this.isRunning = false;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<String> i5 = activeUsers.iterator();
        while (i5.hasNext()) {
            String ac = i5.next();
            if (ac.equals(loggedInUser)) {
                i5.remove();
            }
        }
        Iterator<ClientHandler> i6 = handler.iterator();
        while (i6.hasNext()) {
            ClientHandler ch = i6.next();
            if (ch.loggedInUser.equals(loggedInUser)) {
                i6.remove();
            }
        }
        try {
            broadcastUsers(onlineCommand());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    public StringBuffer onlineCommand() throws IOException {

        StringBuffer buffer = new StringBuffer();
        for (ClientHandler ch : handler) {
            if (ch.isLoggedIn == true) {
                if (buffer.length() != 0){buffer.append(",");}
                buffer.append(ch.loggedInUser);
            }
        }
        return buffer;
    }

    public void broadcastUsers(StringBuffer sb) throws IOException {
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
            if (ch.loggedInUser.equals(username) && ch.isLoggedIn == true) {
                ch.out.writeUTF("MESSAGE#" + loggedInUser + "#" + message);
            }
        }
    }

    public void sendToaSelected(String username) throws IOException {
        String[] usernames = username.split(",");
          for (String selectedUsers:usernames){
                  for (ClientHandler ch : handler) {
                      if (ch.loggedInUser.equals(selectedUsers)) {
                          ch.out.writeUTF("MESSAGE#" + loggedInUser + "#" + message);
                      }
                  }
        }
    }
}



