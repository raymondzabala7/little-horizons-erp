package com.littlehorizons.erp.util;

import com.littlehorizons.erp.model.Payment;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InvoiceGenerator {
    public static void generateElectronicInvoice(Payment payment, String studentName, String parentName) {
        String fileName = "factura_" + payment.getReceiptNumber() + ".txt";
        try {
            Files.createDirectories(Paths.get("invoices"));
            try (PrintWriter pw = new PrintWriter(new FileWriter("invoices/" + fileName))) {
                pw.println("==================================================");
                pw.println("     COLEGIO INTERNACIONAL LITTLE HORIZONS S.A.S.   ");
                pw.println("                 NIT: 900.123.456-1               ");
                pw.println("==================================================");
                pw.println("Factura Electrónica de Venta No: " + payment.getReceiptNumber());
                pw.println("Fecha de Emisión: " + payment.getPaymentDate());
                pw.println("CUFE Simulado: hash-" + payment.getId() + "-900123456");
                pw.println("--------------------------------------------------");
                pw.println("Cliente (Acudiente): " + parentName);
                pw.println("Estudiante: " + studentName);
                pw.println("--------------------------------------------------");
                pw.println("Concepto: " + payment.getPaymentConcept());
                pw.println("Monto Pagado: $" + payment.getAmount());
                pw.println("Método de Pago: " + payment.getPaymentMethod());
                pw.println("==================================================");
                pw.println("Resolución DIAN No. 187640000001 habilitada");
                pw.println("¡Gracias por su pago!");
            }
            AppLogger.info("Factura generada con éxito: " + fileName);
        } catch (IOException e) {
            AppLogger.error("Error generando la factura electrónica", e);
        }
    }
}
