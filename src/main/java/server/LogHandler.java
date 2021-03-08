package server;

import java.io.*;
import java.util.Date;

public class LogHandler {
    Date date;
    File file = null;
    String filename = "";
    FileWriter fileWriter = null;
    String message="";

//    public LogHandler(String message) {
//        this.message = message;
//        addLogToFile(message);
//        System.out.println(message);
//    }

    public LogHandler () {
        filename = "serverlog.txt";
        file = new File(filename);
        try {
            fileWriter = new FileWriter(file, true);
            fileWriter.write("Serverlog starting ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addLog(String message) {
        System.out.println(message);
        this.date = new Date();
        try {
            fileWriter = new FileWriter(file, true);
            fileWriter.write(date.toString() + " : " + message + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}