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
//            er det ikke på sin plads at sætte maks til det antal brugere vi hardcoder? - Jo/NHK


    public Server(int serverPort) {
        this.serverPort = serverPort;

    }

    //    hvorfor er det nu, at det er en fordel at denne proces kører som en tråd?

Scanner scanner = new Scanner(System.in);
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
                //her tildeles en tråd i poolen en clienthanldere der lytter. ny tråd tildels først når ny client connecter
                this.threadPool.execute(new ClientHandler(clientSocket));

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            }
        }



    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    //Stop metode til at lukke serveren
    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException ioException) {
            loghandler.addLog("Serveren blev revet fra hinanden");
            throw new RuntimeException("Serveren blev revet fra hinanden");
        }
    }

    //Åbner serverSocket
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
        //TODO: der skal laves om så der forsøges på args port, ellers standart port. Se Lars eksempel.
        Server server = new Server(9000);
        new Thread(server).start();
        //TODO: Make the server stop on command server.stop();
    }

}
