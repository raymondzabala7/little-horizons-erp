package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.service.StudentService;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.StudentView;

public class StudentController extends BaseController {

    private final StudentService service;
    private final StudentView view = new StudentView();

    public StudentController(StudentService service) {
        this.service = service;
    }

    public void run() {
        int option;
        while ((option = view.menu()) != 0) {
            final int selected = option;
            execute(() -> handle(selected));
        }
    }

    private void handle(int option) {
        switch (option) {
            case 1 -> view.success("Acudiente registrado con ID " + service.registerGuardian(view.promptGuardian()));
            case 2 -> view.success("Estudiante registrado y matriculado con ID " + service.registerStudent(view.promptStudent()));
            case 3 -> view.showStudents(service.listStudents());
            case 4 -> view.showStudents(service.search(InputValidator.readText("Texto a buscar: ", 60)));
            case 5 -> {
                var student = service.getStudent(InputValidator.readInt("ID del estudiante: ", 1, Integer.MAX_VALUE));
                view.showDetail(student, service.principalGuardianOf(student.id()));
            }
            case 6 -> {
                int id = InputValidator.readInt("ID del estudiante: ", 1, Integer.MAX_VALUE);
                String status = InputValidator.readChoice("Nuevo estado: ", Catalog.STUDENT_STATUSES);
                service.changeStatus(id, status);
                view.success("Estado actualizado.");
            }
            case 7 -> view.showGuardians(service.listGuardians());
            case 8 -> view.showStudents(service.studentsOfGuardian(InputValidator.readInt("ID del acudiente: ", 1, Integer.MAX_VALUE)));
            default -> view.error("Opcion no valida.");
        }
    }
}
