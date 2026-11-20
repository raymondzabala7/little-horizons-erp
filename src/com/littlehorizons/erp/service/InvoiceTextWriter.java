package com.littlehorizons.erp.service;

import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.util.SchoolInfo;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Locale;

/** Renders an invoice as a plain text file (the project stores invoices as .txt). */
public class InvoiceTextWriter {

    private static final Path OUTPUT_DIR = Path.of("invoices");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int QR_SIZE = 25;
    private static final int WIDTH = 78;

    /** Simulated CUFE: SHA-384 (96 hex chars) of invoice number + NIT + issue date + total + technical key. */
    public String computeCufe(String number, Invoice draft) {
        String raw = number + SchoolInfo.NIT + draft.issuedAt().format(DATE_TIME) + draft.total().toPlainString() + SchoolInfo.TECHNICAL_KEY;
        return hex("SHA-384", raw);
    }

    public Path write(Invoice invoice, Payment payment, Guardian customer) throws IOException {
        Files.createDirectories(OUTPUT_DIR);
        Path file = OUTPUT_DIR.resolve(String.format("factura_%06d.txt", invoice.id()));
        Files.writeString(file, render(invoice, payment, customer), StandardCharsets.UTF_8);
        return file;
    }

    String render(Invoice invoice, Payment payment, Guardian customer) {
        String line = "=".repeat(WIDTH);
        String thin = "-".repeat(WIDTH);
        String description = payment.conceptName() + (payment.month() != null ? " (mes " + payment.month() + "/" + payment.year() + ")" : "");
        StringBuilder sb = new StringBuilder();
        sb.append(line).append('\n')
          .append(center("FACTURA ELECTRONICA DE VENTA (SIMULADA)")).append('\n')
          .append(center(SchoolInfo.LEGAL_NAME)).append('\n')
          .append(center("NIT " + SchoolInfo.NIT)).append('\n')
          .append(center(SchoolInfo.ADDRESS)).append('\n')
          .append(center("Tel. " + SchoolInfo.PHONE + "  |  " + SchoolInfo.EMAIL)).append('\n')
          .append(line).append('\n')
          .append(String.format("Factura No.      : %s%n", invoice.number()))
          .append(String.format("Fecha de emision : %s%n", invoice.issuedAt().format(DATE_TIME)))
          .append(String.format("Recibo de pago   : %s%n", payment.receiptNumber()))
          .append(String.format("Metodo de pago   : %s%n", payment.method()))
          .append(thin).append('\n')
          .append("CLIENTE (acudiente responsable)\n")
          .append(String.format("Nombre           : %s%n", customer.fullName()))
          .append(String.format("Documento        : %s %s%n", customer.documentType(), customer.documentId()))
          .append(String.format("Direccion        : %s%n", customer.address() == null ? "-" : customer.address()))
          .append(String.format("Estudiante       : %s%n", payment.studentName()))
          .append(thin).append('\n')
          .append(String.format("%-34s %5s %15s %15s%n", "Descripcion", "Cant.", "Valor unit.", "Subtotal"))
          .append(thin).append('\n')
          .append(String.format("%-34s %5d %15s %15s%n", cut(description, 34), 1, money(invoice.subtotal()), money(invoice.subtotal())))
          .append(thin).append('\n')
          .append(String.format("%58s %15s%n", "Subtotal:", money(invoice.subtotal())))
          .append(String.format("%58s %15s%n", "IVA:", money(invoice.tax())))
          .append(String.format("%58s %15s%n", "TOTAL:", money(invoice.total())))
          .append(line).append('\n')
          .append("CUFE (simulado):\n")
          .append(invoice.cufe(), 0, 48).append('\n')
          .append(invoice.cufe(), 48, 96).append('\n')
          .append(thin).append('\n')
          .append(SchoolInfo.RESOLUTION).append('\n')
          .append(thin).append('\n')
          .append("CODIGO QR (simulado):\n")
          .append(qr(invoice)).append('\n')
          .append("Datos del QR: NumFac=").append(invoice.number())
          .append(" NitFac=").append(SchoolInfo.NIT).append(" ValTotal=").append(invoice.total().toPlainString()).append('\n')
          .append(line).append('\n')
          .append(center("Documento generado por el ERP Little Horizons con fines academicos.")).append('\n');
        return sb.toString();
    }

    /** Deterministic pseudo QR built from the CUFE bits, with three finder squares. */
    private String qr(Invoice invoice) {
        byte[] bits = HexFormat.of().parseHex(invoice.cufe());
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < QR_SIZE; y++) {
            for (int x = 0; x < QR_SIZE; x++) {
                sb.append(isDark(x, y, bits) ? "##" : "  ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private boolean isDark(int x, int y, byte[] bits) {
        if (finder(x, y, 0, 0) || finder(x, y, QR_SIZE - 7, 0) || finder(x, y, 0, QR_SIZE - 7)) {
            return finderDark(x, y);
        }
        int index = y * QR_SIZE + x;
        return ((bits[index % bits.length] >> (index % 8)) & 1) == 1;
    }

    private boolean finder(int x, int y, int ox, int oy) {
        return x >= ox - 1 && x <= ox + 7 && y >= oy - 1 && y <= oy + 7;
    }

    private boolean finderDark(int x, int y) {
        int fx = x < 8 ? x : x - (QR_SIZE - 7);
        int fy = y < 8 ? y : y - (QR_SIZE - 7);
        if (fx < 0 || fx > 6 || fy < 0 || fy > 6) {
            return false;
        }
        boolean border = fx == 0 || fx == 6 || fy == 0 || fy == 6;
        boolean core = fx >= 2 && fx <= 4 && fy >= 2 && fy <= 4;
        return border || core;
    }

    private static String money(BigDecimal value) {
        return String.format(Locale.US, "$%,.2f", value);
    }

    private static String center(String text) {
        int pad = Math.max(0, (WIDTH - text.length()) / 2);
        return " ".repeat(pad) + text;
    }

    private static String cut(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 1) + ".";
    }

    private static String hex(String algorithm, String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(algorithm + " not available", e);
        }
    }
}
