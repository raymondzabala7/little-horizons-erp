package com.littlehorizons.erp.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Minimal file logger. Appends entries to logs/error_log.txt.
 * Logging never throws: if the file cannot be written, the entry goes to stderr.
 */
public final class AppLogger {

    private static final Path LOG_FILE = Path.of("logs", "error_log.txt");
    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AppLogger() {
    }

    public static void info(String message) {
        write("INFO", message, null);
    }

    public static void error(String message) {
        write("ERROR", message, null);
    }

    public static void error(String message, Throwable throwable) {
        write("ERROR", message, throwable);
    }

    private static synchronized void write(String level, String message, Throwable throwable) {
        StringBuilder entry = new StringBuilder()
                .append(LocalDateTime.now().format(TIMESTAMP))
                .append(" [").append(level).append("] ")
                .append(message)
                .append(System.lineSeparator());
        if (throwable != null) {
            StringWriter stack = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stack));
            entry.append(stack);
        }
        try {
            Files.createDirectories(LOG_FILE.getParent());
            Files.writeString(LOG_FILE, entry.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Could not write to " + LOG_FILE + ": " + e.getMessage());
            System.err.print(entry);
        }
    }
}