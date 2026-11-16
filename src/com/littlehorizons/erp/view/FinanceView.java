package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;

import java.util.List;

public class FinanceView extends ConsoleView {

    public int menu() {
        return menu("GESTION FINANCIERA", "Listar conceptos de pago", "Crear concepto de pago",
                "Definir valor personalizado para un estudiante", "Generar cobro (pago pendiente)",
                "Confirmar pago y generar factura", "Listar pagos pendientes", "Historial de pagos de un estudiante",
                "Historial de pagos de un acudiente", "Reporte de morosos (mas de un mes)", "Generar factura de un pago ya registrado");
    }

    public void showConcepts(List<PaymentConcept> concepts) {
        table(List.of("ID", "Concepto", "Valor base", "IVA"),
                concepts.stream().map(c -> row(c.id(), c.name(), money(c.baseValue()), c.taxable() ? "19%" : "Exento")).toList());
    }

    public void showPayments(List<Payment> payments) {
        table(List.of("ID", "Estudiante", "Concepto", "Valor", "Vence", "Pagado", "Metodo", "Recibo", "Estado"),
                payments.stream().map(p -> row(p.id(), p.studentName(), p.conceptName(), money(p.amount()), p.dueDate(),
                        p.paymentDate(), p.method(), p.receiptNumber(), p.status())).toList());
    }

    public void showInvoice(Invoice invoice) {
        success("Factura LH-" + String.format("%06d", invoice.number()) + " generada. Total: " + money(invoice.total()));
        info("Archivo guardado en: " + invoice.filePath());
    }
}
