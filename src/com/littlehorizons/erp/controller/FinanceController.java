package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.service.FinanceService;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.FinanceView;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    private static int year() {
        return InputValidator.readInt("Anio academico: ", 2000, LocalDate.now().getYear() + 1);
    }

    private void handle(int option) {
        switch (option) {
            case 1 -> view.showConcepts(service.listConcepts());
            case 2 -> {
                String code = InputValidator.readText("Codigo del concepto (ej. EXCURSION): ", 20);
                String name = InputValidator.readText("Nombre del concepto: ", 100);
                BigDecimal value = InputValidator.readDecimal("Valor base: ", BigDecimal.ONE, MAX_AMOUNT, 2);
                boolean recurring = InputValidator.readYesNo("Es un cobro mensual?");
                view.success("Concepto creado con ID " + service.createConcept(code, name, value, recurring));
            }
            case 3 -> {
                int studentId = id("ID del estudiante: ");
                view.showConcepts(service.listConcepts());
                int conceptId = id("ID del concepto: ");
                int year = year();
                BigDecimal value = InputValidator.readDecimal("Valor personalizado: ", BigDecimal.ONE, MAX_AMOUNT, 2);
                String reason = InputValidator.readText("Motivo (descuento por hermanos, beca...): ", 150);
                service.setCustomValue(studentId, conceptId, year, value, reason);
                view.success("Valor personalizado guardado.");
            }
            case 4 -> {
                int studentId = id("ID del estudiante: ");
                view.showConcepts(service.listConcepts());
                int conceptId = id("ID del concepto: ");
                int year = year();
                Integer month = service.getConcept(conceptId).recurring()
                        ? Integer.valueOf(InputValidator.readInt("Mes que cubre el pago (1-12): ", 1, 12)) : null;
                String method = InputValidator.readChoice("Metodo de pago: ", Catalog.PAYMENT_METHODS);
                view.showInvoice(service.registerPayment(studentId, conceptId, method, month, year));
            }
            case 5 -> view.showPayments(service.historyOfStudent(id("ID del estudiante: ")));
            case 6 -> view.showPayments(service.historyOfGuardian(id("ID del acudiente: ")));
            case 7 -> view.showDelinquents(service.delinquents());
            case 8 -> {
                view.showPayments(service.paymentsWithoutInvoice());
                view.showInvoice(service.generateInvoice(id("ID del pago a facturar: ")));
            }
            default -> view.error("Opcion no valida.");
        }
    }
}
