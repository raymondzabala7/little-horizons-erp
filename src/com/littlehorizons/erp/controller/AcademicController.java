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
                String code = InputValidator.readText("Codigo de la materia (ej. MAT-05): ", 20);
                String name = InputValidator.readText("Nombre de la materia: ", 100);
                int grade = InputValidator.readInt("Grado (0 = Preescolar, 1 a 11): ", 0, 11);
                view.success("Materia creada con ID " + service.createSubject(code, name, grade));
            }
            case 3 -> {
                int studentId = id("ID del estudiante: ");
                int subjectId = id("ID de la materia: ");
                int year = year();
                service.enroll(studentId, subjectId, year, InputValidator.readChoice("Periodo: ", Catalog.ENROLLMENT_PERIODS));
                view.success("Estudiante inscrito en la materia.");
            }
            case 4 -> {
                int studentId = id("ID del estudiante: ");
                int year = year();
                String period = InputValidator.readChoice("Periodo: ", Catalog.ENROLLMENT_PERIODS);
                view.success("Inscripciones nuevas: " + service.enrollAllOfGrade(studentId, year, period));
            }
            case 5 -> {
                int studentId = id("ID del estudiante: ");
                int subjectId = id("ID de la materia: ");
                int teacherId = id("ID del profesor que registra la nota: ");
                String period = InputValidator.readChoice("Periodo: ", Catalog.PERIODS);
                int year = year();
                BigDecimal grade = InputValidator.readDecimal("Nota (0.0 a 5.0): ", BigDecimal.ZERO, new BigDecimal("5.0"), 1);
                service.recordGrade(studentId, subjectId, teacherId, period, year, grade);
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
