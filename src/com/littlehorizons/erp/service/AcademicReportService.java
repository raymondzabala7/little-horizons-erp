package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.AcademicGradeDao;
import com.littlehorizons.erp.model.AcademicGrade;
import com.littlehorizons.erp.model.PerformanceReport;
import com.littlehorizons.erp.model.SubjectPerformance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Academic performance per subject. The average covers every grade of the subject regardless of
 * student or period, rounded to one decimal; ties are decided on that rounded value.
 */
public class AcademicReportService {

    private final AcademicGradeDao gradeDao;

    public AcademicReportService(AcademicGradeDao gradeDao) {
        this.gradeDao = gradeDao;
    }

    public PerformanceReport buildReport() {
        List<SubjectPerformance> ranking = gradeDao.findAll().stream()
                .collect(Collectors.groupingBy(AcademicGrade::subjectId))
                .values().stream()
                .map(AcademicReportService::toPerformance)
                .sorted(Comparator.comparing(SubjectPerformance::average).reversed()
                        .thenComparing(SubjectPerformance::name))
                .toList();
        if (ranking.isEmpty()) {
            return new PerformanceReport(List.of(), List.of());
        }
        BigDecimal best = ranking.get(0).average();
        List<SubjectPerformance> top = ranking.stream()
                .filter(p -> p.average().compareTo(best) == 0)
                .toList();
        return new PerformanceReport(ranking, top);
    }

    private static SubjectPerformance toPerformance(List<AcademicGrade> grades) {
        AcademicGrade first = grades.get(0);
        BigDecimal sum = grades.stream().map(AcademicGrade::grade).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = sum.divide(BigDecimal.valueOf(grades.size()), 1, RoundingMode.HALF_UP);
        return new SubjectPerformance(first.subjectId(), first.subjectCode(), first.subjectName(), average);
    }
}
