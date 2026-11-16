package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.FinancialController;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.exception.InputCancelledException;

import java.time.LocalDate;

public class FinancialView {
    private static final FinancialController controller = new FinancialController();

    public static void showMenu() {
        try {
            while (true) {
                System.out.println("\n--- GESTIÓN FINANCIERA Y FACTURACIÓN ---");
                System.out.println("1. Registrar Pago y Generar Factura Electrónica");
                System.out.println("2. Volver al Menú Principal");
                
                int option = InputValidator.getInt("Seleccione una opción");
                if (option == 1) {
                    Payment p = new Payment();
                    p.setStudentId(InputValidator.getInt("ID del Estudiante"));
                    p.setPaymentConcept(InputValidator.getString("Concepto (Matrícula, Pensión, etc.)"));
                    p.setAmount(Double.parseDouble(InputValidator.getString("Monto")));
                    p.setPaymentDate(LocalDate.now());
                    p.setPaymentMethod(InputValidator.getString("Método de Pago (Efectivo/Transferencia/Tarjeta)"));
                    p.setReceiptNumber(InputValidator.getString("Número de Recibo (ej. REC-001)"));

                    String studentName = InputValidator.getString("Nombre completo del Estudiante");
                    String parentName = InputValidator.getString("Nombre completo del Acudiente");

                    if (controller.processPayment(p, studentName, parentName)) {
                        System.out.println("¡Pago registrado y Factura Electrónica generada en la carpeta 'invoices/' con éxito!");
                    } else {
                        System.out.println("Error al registrar el pago.");
                    }
                } else if (option == 2) {
                    break;
                }
            }
        } catch (InputCancelledException e) {
            System.out.println("\n[Operación cancelada]");
        }
    }
}
