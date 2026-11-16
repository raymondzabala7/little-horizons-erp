package com.littlehorizons.erp.model;

import java.math.BigDecimal;

public record AcademicRecord(int id, int studentId, int subjectId, String subjectName, String period,
                             int academicYear, BigDecimal grade) { }
