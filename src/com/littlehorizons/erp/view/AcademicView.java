package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.AbsenceSummary;
import com.littlehorizons.erp.model.AcademicRecord;
import com.littlehorizons.erp.model.Attendance;
import com.littlehorizons.erp.model.Subject;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

public class AcademicView extends ConsoleView {

    public int menu() {
        return menu("GESTION ACADEMICA", "Listar materias", "Crear materia", "Inscribir estudiante en una materia",
                "Inscribir estudiante en todas las materias de su grado", "Registrar nota", "Historial academico de un estudiante",
                "Promedios (materia, periodo, curso)", "Registrar asistencia", "Ver asistencia de un estudiante",
                "Reporte de inasistencias (semana)", "Generar Reporte de Rendimiento Academico por Materia");
    }

    public void showSubjects(List<Subject> subjects) {
        table(List.of("ID", "Codigo", "Materia", "Grado"),
                subjects.stream().map(s -> row(s.id(), s.code(), s.name(), s.grade() == 0 ? "Preescolar" : s.grade() + "°")).toList());
    }

    public void showHistory(List<AcademicRecord> records) {
        table(List.of("Anio", "Materia", "Periodo", "Nota"),
                records.stream().map(r -> row(r.academicYear(), r.subjectName(), r.period(), r.grade())).toList());
    }

    public void showAverages(Map<String, Double> perSubject, Map<String, OptionalDouble> perPeriod, OptionalDouble year) {
        info("\nPromedio por materia:");
        table(List.of("Materia", "Promedio"), perSubject.entrySet().stream()
                .map(e -> row(e.getKey(), String.format("%.2f", e.getValue()))).toList());
        info("\nPromedio general por periodo:");
        table(List.of("Periodo", "Promedio"), perPeriod.entrySet().stream()
                .map(e -> row(e.getKey(), e.getValue().isPresent() ? String.format("%.2f", e.getValue().getAsDouble()) : "sin notas")).toList());
        info("\nPromedio general del anio: " + (year.isPresent() ? String.format("%.2f", year.getAsDouble()) : "sin notas"));
    }

    public void showCourseAverage(String subject, OptionalDouble avg) {
        info("Promedio del curso en " + subject + ": " + (avg.isPresent() ? String.format("%.2f", avg.getAsDouble()) : "sin notas"));
    }

    public void showAttendance(List<Attendance> list) {
        table(List.of("Fecha", "Materia ID", "Estado"), list.stream().map(a -> row(a.date(), a.subjectId(), a.status())).toList());
    }

    public void showAbsences(List<AbsenceSummary> list) {
        info("Estudiantes con mas de 3 ausencias en la semana:");
        table(List.of("ID", "Estudiante", "Ausencias"), list.stream().map(a -> row(a.studentId(), a.studentName(), a.absences())).toList());
    }
}
