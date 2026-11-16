package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.service.FinanceService;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.FinanceView;

import java.math.BigDecimal;

public class FinanceController extends BaseController {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("99999999.99");

    private final FinanceService service;
    private final FinanceView view = new FinanceView();

    public FinanceController(FinanceService service) {
        this.service = service;
    }

    public void run() {
        int option;
        while ((option = view.menu()) != 0) {
            final int selected = option;
            execute(() -> handle(selected));
        }
    }

    private static int id(String label) {
        return InputValidator.readInt(label, 1, Integer.MAX_VALUE);
    }

    private void handle(int option) {
        switch (option) {
            case 1 -> view.showConcepts(service.listConcepts());
            case 2 -> {
                String name = InputValidator.readText("Nombre del concepto: ", 80);
                BigDecimal value = InputValidator.readDecimal("Valor base: ", BigDecimal.ONE, MAX_AMOUNT, 2);
                boolean taxable = InputValidator.readYesNo("Aplica IVA 19%?");
                view.success("Concepto creado con ID " + service.createConcept(name, value, taxable));
            }
            case 3 -> {
                int studentId = id("ID del estudiante: ");
                view.showConcepts(service.listConcepts());
                int conceptId = id("ID del concepto: ");
                BigDecimal value = InputValidator.readDecimal("Valor personalizado: ", BigDecimal.ONE, MAX_AMOUNT, 2);
                String reason = InputValidator.readText("Motivo (descuento por hermanos, beca...): ", 100);
                service.setCustomValue(studentId, conceptId, value, reason);
                view.success("Valor personalizado guardado.");
            }
            case 4 -> {
                int studentId = id("ID del estudiante: ");
                view.showConcepts(service.listConcepts());
                int conceptId = id("ID del concepto: ");
                var due = InputValidator.readDate("Fecha de vencimiento");
                view.success("Cobro creado con ID de pago " + service.createCharge(studentId, conceptId, due));
            }
            case 5 -> {
                int paymentId = id("ID del pago pendiente: ");
                String method = InputValidator.readChoice("Metodo de pago: ", Catalog.PAYMENT_METHODS);
                view.showInvoice(service.confirmPayment(paymentId, method));
            }
            case 6 -> view.showPayments(service.pendingPayments());
            case 7 -> view.showPayments(service.historyOfStudent(id("ID del estudiante: ")));
            case 8 -> view.showPayments(service.historyOfGuardian(id("ID del acudiente: ")));
            case 9 -> view.showPayments(service.delinquentPayments());
            case 10 -> {
                view.showPayments(service.paidWithoutInvoice());
                view.showInvoice(service.generateInvoice(id("ID del pago a facturar: ")));
            }
            default -> view.error("Opcion no valida.");
        }
    }
}
