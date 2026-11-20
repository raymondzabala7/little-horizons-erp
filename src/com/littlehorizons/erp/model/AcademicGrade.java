package com.littlehorizons.erp.model;

import java.math.BigDecimal;

/** One academic record joined with its subject, as read for the performance report. */
public record AcademicGrade(int recordId, BigDecimal grade, String period, int subjectId, String subjectCode,
                            String subjectName) { }
