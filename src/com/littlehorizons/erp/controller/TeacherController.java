package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.service.AcademicService;
import com.littlehorizons.erp.service.TeacherService;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.AcademicView;
import com.littlehorizons.erp.view.TeacherView;

public class TeacherController extends BaseController {

    private final TeacherService service;
    private final AcademicService academicService;
    private final TeacherView view = new TeacherView();
    private final AcademicView academicView = new AcademicView();

    public TeacherController(TeacherService service, AcademicService academicService) {
        this.service = service;
        this.academicService = academicService;
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
            case 1 -> view.success("Profesor registrado con ID " + service.register(view.promptTeacher()));
            case 2 -> view.showTeachers(service.list());
            case 3 -> {
                int teacherId = InputValidator.readInt("ID del profesor: ", 1, Integer.MAX_VALUE);
                academicView.showSubjects(academicService.listSubjects());
                int subjectId = InputValidator.readInt("ID de la materia: ", 1, Integer.MAX_VALUE);
                String day = InputValidator.readChoice("Dia: ", Catalog.WEEK_DAYS);
                var start = InputValidator.readTime("Hora de inicio");
                var end = InputValidator.readTime("Hora de fin");
                service.assignSubject(teacherId, subjectId, day, start, end);
                view.success("Materia asignada.");
            }
            case 4 -> view.showAssignments(service.assignments(InputValidator.readInt("ID del profesor: ", 1, Integer.MAX_VALUE)));
            default -> view.error("Opcion no valida.");
        }
    }
}
