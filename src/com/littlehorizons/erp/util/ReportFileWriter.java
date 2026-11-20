package com.littlehorizons.erp.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Saves report text as a .txt file inside the reports/ folder with a unique, timestamped name. */
public final class ReportFileWriter {

    private static final Path REPORTS_DIR = Path.of("reports");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");

    private ReportFileWriter() { }

    public static Path write(String baseName, String content) throws IOException {
        Files.createDirectories(REPORTS_DIR);
        Path file = REPORTS_DIR.resolve(baseName + "_" + LocalDateTime.now().format(STAMP) + ".txt");
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }
}
