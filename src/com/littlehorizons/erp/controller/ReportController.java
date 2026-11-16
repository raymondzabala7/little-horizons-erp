package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.service.ReportService;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.ReportView;

import java.time.LocalDate;

public class ReportController extends BaseController {

    private final ReportService service;
    private final ReportView view = new ReportView();

    public ReportController(ReportService service) {
        this.service = service;
    }

    public void run() {
        int option;
        while ((option = view.menu()) != 0) {
            final int selected = option;
            execute(() -> handle(selected));
        }
    }

    private static int year() {
        return InputValidator.readInt("Anio academico: ", 2000, LocalDate.now().getYear() + 1);
    }

    private void handle(int option) {
        switch (option) {
            case 1 -> view.showCounts("Grado", service.activeStudentsPerGrade());
            case 2 -> view.showAverages(service.topStudents(year(), InputValidator.readInt("Cuantos mostrar (1-50): ", 1, 50)));
            case 3 -> view.showAverages(service.studentsNeedingSupport(year(), 3.0));
            case 4 -> view.showCounts("Evento", service.participationPerEvent());
            case 5 -> view.showClubs(service.clubOccupancy());
            case 6 -> {
                view.showMoney("Metodo de pago", service.collectedPerMethod());
                view.showMoney("Concepto", service.collectedPerConcept());
                view.showTotal("Cartera pendiente total", service.totalPending());
            }
            case 7 -> view.showCounts("Estado", service.resourcesPerStatus());
            default -> view.error("Opcion no valida.");
        }
    }
}
