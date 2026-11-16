package com.littlehorizons.erp.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class AppLogger {
    private static final String LOG_FILE = "logs/error_log.txt";

    public static void info(String message) {
        log("INFO", message, null);
    }

    public static void error(String message, Exception e) {
        log("ERROR", message, e);
    }

    private static synchronized void log(String level, String message, Exception e) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("[" + LocalDateTime.now() + "] [" + level + "] " + message);
            if (e != null) {
                e.printStackTrace(pw);
            }
        } catch (IOException ioException) {
            System.err.println("Could not write to log file: " + ioException.getMessage());
        }
    }
}
