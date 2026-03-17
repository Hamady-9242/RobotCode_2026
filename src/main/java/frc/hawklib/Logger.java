package frc.hawklib;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Utility class for uniform formatted console logging */
public class Logger {
    private Logger() { throw new AssertionError("This is a utility class!"); }

    public static String getFormattedTimestamp() { return getFormattedTimestamp(new Date()); }
    public static String getFormattedTimestamp(Date time) { return new SimpleDateFormat("HH:mm:ss.SSS").format(time); }

    public static void printSeparator() {
        System.out.println("--------------------------------------------------");
    }

    public static void printHeader(String header) {
        printSeparator();
        System.out.println("[" + header + "]");
        printSeparator();
    }

    public static void printMessage(String message) {
        System.out.println("[LOG][" + getFormattedTimestamp() + "] " + message);
    }

    public static void printError(String errorMessage) {
        System.err.println("[ERR][" + getFormattedTimestamp() + "] " + errorMessage);
    }
}
