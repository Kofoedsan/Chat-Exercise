package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server implements Runnable {

    protected ServerSocket serverSocket = null;
    protected int serverPort;
    protected Socket clientSocket = null;
    protected Thread runningThread = null;
    protected boolean isStopped = false;
    public LogHandler loghandler = null;


    protected ExecutorService threadPool = Executors.newFixedThreadPool(12);



    public Server(int serverPort) {
        this.serverPort = serverPort;

    }

    @Override
    public void run() {

        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }

        loghandler = new LogHandler();
        loghandler.addLog("server started");

        openSS();
        while (!isStopped()) {
            try {
                clientSocket = this.serverSocket.accept();
                loghandler.addLog("client with adress " + clientSocket.getInetAddress() + " connected.");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            try {
                this.threadPool.execute(new ClientHandler(clientSocket));

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            }
        }



    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void serverStop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException ioException) {
            loghandler.addLog("Serveren blev revet fra hinanden");
            throw new RuntimeException("Serveren blev revet fra hinanden");
        }
    }


    private void openSS() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
            loghandler.addLog("server listening at port " + serverPort);

        } catch (IOException ioException) {
            loghandler.addLog("Error opening port : " + serverPort);
            throw new RuntimeException("Error opening port : " + serverPort, ioException);
        }
    }


    public static void main(String[] args) {
        int port;
        try {
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
            }
            else {
                throw new IllegalArgumentException("# Server not provided with the right arguments");
            }
        } catch (NumberFormatException ne) {
            System.out.println("# Illegal inputs provided when starting the server!");
            return;
        }
        Server server = new Server(port);
        new Thread(server).start();
    }

}
