package server;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class LogHandler {

    public LogHandler(String l) {

    }

    public static void logfile(String l) {
//        ny fil hver gang
        File file = new File("Serverlog.txt");

        Date date = new Date();
//        StringWriter logLine = new StringWriter();

        FileWriter fileWriter = new FileWriter(file, true);
        fileWriter.write(date.toString() + " : " + l + "\n" );
        fileWriter.close();
    }
}