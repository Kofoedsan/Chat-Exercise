package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{

    protected ServerSocket serverSocket = null;
    protected int serverPort;
    protected Socket clientSocket = null;
    protected Thread runningThread= null;
    protected boolean isStopped    = false;

    protected ExecutorService threadPool =
            Executors.newFixedThreadPool(10);

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {

        synchronized (this){
            this.runningThread = Thread.currentThread();
        }
        openSS();
        while(!isStopped()){
            try {
                clientSocket = this.serverSocket.accept();
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

    public static void main(String[]args){
        Server server = new Server(8080);
        new Thread(server).start();
       //TODO: Make the server stop on command server.stop();
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }
    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
    private void openSS(){
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException ioException) {
            throw new RuntimeException("Error opening port : " + serverPort, ioException);
        }
    }
}