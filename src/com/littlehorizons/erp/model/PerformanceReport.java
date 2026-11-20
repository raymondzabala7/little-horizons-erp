package com.littlehorizons.erp.model;

import java.util.List;

/** ranking is sorted from highest to lowest average; topSubjects holds every subject tied for the best average. */
public record PerformanceReport(List<SubjectPerformance> ranking, List<SubjectPerformance> topSubjects) {
    public boolean isEmpty() {
        return ranking.isEmpty();
    }
}
