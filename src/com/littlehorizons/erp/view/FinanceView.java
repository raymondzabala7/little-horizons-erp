package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Delinquent;
import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;

import java.util.List;

public class FinanceView extends ConsoleView {

    public int menu() {
        return menu("GESTION FINANCIERA", "Listar conceptos de pago", "Crear concepto de pago",
                "Definir valor personalizado para un estudiante (beca, hermanos)", "Registrar pago y generar factura",
                "Historial de pagos de un estudiante", "Historial de pagos de un acudiente",
                "Reporte de morosos (pension con mas de un mes de atraso)", "Generar factura de un pago ya registrado");
    }

    public void showConcepts(List<PaymentConcept> concepts) {
        table(List.of("ID", "Codigo", "Concepto", "Valor base", "Mensual"),
                concepts.stream().map(c -> row(c.id(), c.code(), c.name(), money(c.baseValue()), c.recurring() ? "Si" : "No")).toList());
    }

    public void showPayments(List<Payment> payments) {
        table(List.of("ID", "Estudiante", "Concepto", "Valor", "Fecha", "Metodo", "Recibo", "Mes/Anio"),
                payments.stream().map(p -> row(p.id(), p.studentName(), p.conceptName(), money(p.amount()), p.paymentDate(),
                        p.method(), p.receiptNumber(), p.month() == null ? p.year() : p.month() + "/" + p.year())).toList());
    }

    public void showDelinquents(List<Delinquent> list) {
        table(List.of("ID", "Estudiante", "Grado", "Meses sin pagar", "Valor adeudado"),
                list.stream().map(d -> row(d.studentId(), d.studentName(), d.gradeLabel(), d.overdueMonths(), money(d.amountDue()))).toList());
    }

    public void showInvoice(Invoice invoice) {
        success("Factura " + invoice.number() + " generada.");
        info("Archivo guardado en: " + invoice.filePath());
    }
}
