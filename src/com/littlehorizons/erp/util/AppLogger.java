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

/** Minimal file logger writing to logs/error_log.txt. */
public final class AppLogger {

    private static final Path LOG_FILE = Path.of("logs", "error_log.txt");
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AppLogger() { }

    public static void info(String message) { write("INFO", message, null); }

    public static void warn(String message) { write("WARN", message, null); }

    public static void error(String message, Throwable error) { write("ERROR", message, error); }

    private static synchronized void write(String level, String message, Throwable error) {
        StringBuilder line = new StringBuilder()
                .append(LocalDateTime.now().format(TS)).append(" [").append(level).append("] ").append(message);
        if (error != null) {
            StringWriter sw = new StringWriter();
            error.printStackTrace(new PrintWriter(sw));
            line.append(System.lineSeparator()).append(sw);
        }
        line.append(System.lineSeparator());
        try {
            Files.createDirectories(LOG_FILE.getParent());
            Files.writeString(LOG_FILE, line.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Could not write log file: " + e.getMessage());
        }
    }
}
