package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.model.Loan;
import com.littlehorizons.erp.service.ResourceService;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.ResourceView;

import java.util.List;

public class ResourceController extends BaseController {

    private final ResourceService service;
    private final ResourceView view = new ResourceView();

    public ResourceController(ResourceService service) {
        this.service = service;
    }

    public void run() {
        int option;
        while ((option = view.menu()) != 0) {
            final int selected = option;
            execute(() -> handle(selected));
        }
    }

    private static String borrowerType() {
        return InputValidator.readChoice("Tipo de prestatario: ", List.of(Loan.STUDENT, Loan.TEACHER));
    }

    private void handle(int option) {
        switch (option) {
            case 1 -> view.showResources(service.listResources());
            case 2 -> view.showResources(service.availableResources());
            case 3 -> {
                String code = InputValidator.readText("Codigo unico (ej. LIB-050): ", 50);
                String name = InputValidator.readText("Nombre: ", 150);
                String category = InputValidator.readChoice("Categoria: ", service.categories());
                String location = InputValidator.readText("Ubicacion fisica: ", 100);
                view.success("Recurso registrado con ID " + service.register(code, name, category, location));
            }
            case 4 -> {
                String code = InputValidator.readText("Codigo del recurso: ", 50);
                String type = borrowerType();
                int borrowerId = InputValidator.readInt("ID del " + type.toLowerCase() + ": ", 1, Integer.MAX_VALUE);
                var due = InputValidator.readDate("Fecha de devolucion programada");
                service.lend(code, type, borrowerId, due);
                view.success("Prestamo registrado.");
            }
            case 5 -> {
                service.returnResource(InputValidator.readText("Codigo del recurso: ", 50));
                view.success("Devolucion registrada. El recurso vuelve a estar disponible.");
            }
            case 6 -> view.showLoans(service.openLoans());
            case 7 -> view.showLoans(service.overdueLoans());
            case 8 -> {
                String code = InputValidator.readText("Codigo del recurso: ", 50);
                String status = InputValidator.readChoice("Nuevo estado: ", List.of("Disponible", "En Mantenimiento", "Dado de Baja"));
                service.changeStatus(code, status);
                view.success("Estado actualizado.");
            }
            case 9 -> {
                String type = borrowerType();
                view.showLoans(service.loansOf(type, InputValidator.readInt("ID: ", 1, Integer.MAX_VALUE)));
            }
            default -> view.error("Opcion no valida.");
        }
    }
}
