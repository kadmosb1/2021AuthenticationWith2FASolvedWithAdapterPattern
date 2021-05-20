package logging;

import login.Authentication;
import login.AuthenticationSimple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logging {

    private static Logging singleton;
    private File logFile;

    protected String getFormattedDate () {
        return LocalDate.now ().format (DateTimeFormatter.ofPattern ("dd-MM-yyyy"));
    }

    protected String getFormattedDateAndTime() {
        return getFormattedDate () + LocalDateTime.now ().format (DateTimeFormatter.ofPattern (" hh:mm:ss"));
    }

    private Logging () {
    }

    public static Logging getInstance () {

        if (singleton == null) {
            singleton = new Logging ();
        }

        return singleton;
    }

    private void setLogFilename () {
        logFile = new File ("src\\main\\resources\\logging\\" + getFormattedDate () + ".log");
    }

    protected boolean logFileDoesNotExist () {
        return !logFile.exists ();
    }

    protected String getLogString (String message) {

        String header = "";
        setLogFilename ();

        if (logFileDoesNotExist ()) {
            header = String.format ("%-19s %-20s %s%n", "Date", "Gebruikersnaam", "Logging");
        }

        String pre = String.format ("%-19s ", getFormattedDateAndTime());
        pre += String.format ("%-20s ", AuthenticationSimple.getInstance ().getUserNameOfActiveUser ());

        return String.format ("%s%s%s%n", header, pre, message);
    }

    public void printLog (String message) {

        try {
            String logString = getLogString (message);
            PrintWriter writer = new PrintWriter (new FileOutputStream (logFile,true));
            writer.append (logString);
            writer.close ();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace ();
        }
    }

    public void printLog (Exception e) {

        StackTraceElement [] elements = e.getStackTrace ();
        StringBuilder message = new StringBuilder ();
        message.append (e.getMessage ());

        for (StackTraceElement element : elements) {
            message.append ("\r\n");
            message.append (element.toString ());
        }

        if (!message.toString ().contains ("Logging")) {
            printLog (message.toString ());
        }
    }
}
