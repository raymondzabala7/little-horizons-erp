package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.service.AcademicService;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.AcademicView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.OptionalDouble;
import java.util.TreeMap;

public class AcademicController extends BaseController {

    private final AcademicService service;
    private final AcademicView view = new AcademicView();

    public AcademicController(AcademicService service) {
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
            case 1 -> view.showSubjects(service.listSubjects());
            case 2 -> {
                String name = InputValidator.readText("Nombre de la materia: ", 80);
                int grade = InputValidator.readInt("Grado (0 = Preescolar, 1 a 11): ", 0, 11);
                view.success("Materia creada con ID " + service.createSubject(name, grade));
            }
            case 3 -> {
                int studentId = id("ID del estudiante: ");
                int subjectId = id("ID de la materia: ");
                service.enroll(studentId, subjectId, year());
                view.success("Estudiante inscrito en la materia.");
            }
            case 4 -> {
                int added = service.enrollAllOfGrade(id("ID del estudiante: "), year());
                view.success("Inscripciones nuevas: " + added);
            }
            case 5 -> {
                int studentId = id("ID del estudiante: ");
                int subjectId = id("ID de la materia: ");
                String period = InputValidator.readChoice("Periodo: ", Catalog.PERIODS);
                int year = year();
                BigDecimal grade = InputValidator.readDecimal("Nota (0.0 a 5.0): ", BigDecimal.ZERO, new BigDecimal("5.0"), 1);
                service.recordGrade(studentId, subjectId, period, year, grade);
                view.success("Nota registrada.");
            }
            case 6 -> view.showHistory(service.history(id("ID del estudiante: ")));
            case 7 -> showAverages();
            case 8 -> {
                int studentId = id("ID del estudiante: ");
                int subjectId = id("ID de la materia: ");
                LocalDate date = InputValidator.readDate("Fecha");
                String status = InputValidator.readChoice("Estado: ", Catalog.ATTENDANCE_STATUSES);
                service.markAttendance(studentId, subjectId, date, status);
                view.success("Asistencia registrada.");
            }
            case 9 -> view.showAttendance(service.attendanceOf(id("ID del estudiante: ")));
            case 10 -> view.showAbsences(service.frequentAbsences(InputValidator.readDate("Fecha del lunes de la semana")));
            default -> view.error("Opcion no valida.");
        }
    }

    private void showAverages() {
        int studentId = id("ID del estudiante: ");
        int year = year();
        var perPeriod = new TreeMap<String, OptionalDouble>();
        Catalog.PERIODS.forEach(p -> perPeriod.put(p, service.periodAverage(studentId, p, year)));
        view.showAverages(service.subjectAverages(studentId, year), perPeriod, service.yearAverage(studentId, year));
        if (InputValidator.readYesNo("Consultar el promedio de un curso?")) {
            int subjectId = id("ID de la materia: ");
            view.showCourseAverage(service.getSubject(subjectId).label(), service.courseAverage(subjectId, year));
        }
    }
}
