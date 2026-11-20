package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.StudentAverage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ReportView extends ConsoleView {

    public int menu() {
        return menu("REPORTES Y ESTADISTICAS", "Estudiantes activos por grado", "Mejores promedios del anio",
                "Estudiantes que necesitan apoyo (promedio < 3.0)", "Participacion por evento", "Ocupacion de clubes",
                "Recaudo por metodo y por concepto, y cartera en mora", "Recursos por estado");
    }

    public void showCounts(String keyHeader, Map<String, Long> counts) {
        table(List.of(keyHeader, "Cantidad"), counts.entrySet().stream().map(e -> row(e.getKey(), e.getValue())).toList());
    }

    public void showAverages(List<StudentAverage> list) {
        table(List.of("ID", "Estudiante", "Grado", "Promedio"),
                list.stream().map(a -> row(a.studentId(), a.studentName(), a.gradeLabel(), String.format("%.2f", a.average()))).toList());
    }

    public void showClubs(List<Club> clubs) {
        table(List.of("Club", "Activos", "Cupo", "Ocupacion"), clubs.stream()
                .map(c -> row(c.name(), c.activeMembers(), c.maxCapacity() == 0 ? "sin limite" : c.maxCapacity(),
                        c.maxCapacity() == 0 ? "-" : String.format("%.0f%%", 100.0 * c.activeMembers() / c.maxCapacity()))).toList());
    }

    public void showMoney(String keyHeader, Map<String, BigDecimal> amounts) {
        table(List.of(keyHeader, "Total"), amounts.entrySet().stream().map(e -> row(e.getKey(), money(e.getValue()))).toList());
    }

    public void showTotal(String label, BigDecimal amount) {
        info("\n" + label + ": " + money(amount));
    }
}
