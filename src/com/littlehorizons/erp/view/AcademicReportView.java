package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.PerformanceReport;
import com.littlehorizons.erp.model.SubjectPerformance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Builds the text of the performance report (shown on the console and saved to a .txt file). */
public class AcademicReportView extends ConsoleView {

    private static final String DOUBLE_LINE = "=".repeat(70);
    private static final String SINGLE_LINE = "-".repeat(70);
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String render(PerformanceReport report, int year) {
        StringBuilder sb = new StringBuilder();
        sb.append(DOUBLE_LINE).append('\n')
          .append("  REPORTE DE RENDIMIENTO ACADEMICO POR MATERIA - ").append(year).append('\n')
          .append(DOUBLE_LINE).append('\n')
          .append("Generado: ").append(LocalDateTime.now().format(STAMP)).append("\n\n");
        if (report.isEmpty()) {
            sb.append(DOUBLE_LINE).append('\n')
              .append("No hay registros academicos suficientes para generar estadisticas.\n")
              .append("Por favor, asegurese de que existan notas registradas en el sistema.\n")
              .append(DOUBLE_LINE).append('\n');
            return sb.toString();
        }
        sb.append(SINGLE_LINE).append('\n')
          .append("PROMEDIO GENERAL POR MATERIA (Ordenado de Mayor a Menor)\n")
          .append(SINGLE_LINE).append("\n\n");
        int codeWidth = report.ranking().stream().mapToInt(p -> p.code().length()).max().orElse(6);
        int nameWidth = report.ranking().stream().mapToInt(p -> p.name().length()).max().orElse(10);
        report.ranking().forEach(p -> sb.append(String.format("Codigo: %-" + codeWidth + "s | Materia: %-" + nameWidth
                + "s | Promedio: %s%n", p.code(), p.name(), p.average().toPlainString())));
        sb.append('\n').append(SINGLE_LINE).append('\n')
          .append("  MATERIA(S) CON MEJOR RENDIMIENTO\n")
          .append(SINGLE_LINE).append("\n\n");
        List<SubjectPerformance> top = report.topSubjects();
        if (top.size() == 1) {
            sb.append("*** MATERIA DESTACADA: ").append(top.get(0).name()).append('\n')
              .append("    Promedio General: ").append(top.get(0).average().toPlainString()).append('\n')
              .append("    Esta materia presenta el mejor rendimiento academico del colegio.\n");
        } else {
            sb.append("*** MATERIAS DESTACADAS (Empate):\n\n");
            int topWidth = top.stream().mapToInt(p -> p.name().length()).max().orElse(10);
            top.forEach(p -> sb.append(String.format("    - %-" + topWidth + "s | Promedio: %s%n", p.name(), p.average().toPlainString())));
            sb.append("\n    Estas materias comparten el mejor rendimiento academico del colegio.\n");
        }
        sb.append('\n').append(DOUBLE_LINE).append('\n')
          .append("Reporte generado exitosamente.\n")
          .append(DOUBLE_LINE).append('\n');
        return sb.toString();
    }

    public void show(String text, String savedPath) {
        System.out.println("\n" + text);
        info("Reporte guardado en: " + savedPath);
    }
}
