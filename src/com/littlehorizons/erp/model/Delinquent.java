package com.littlehorizons.erp.model;

import java.math.BigDecimal;
import java.util.List;

public record Delinquent(int studentId, String studentName, String gradeLabel, List<Integer> overdueMonths,
                         BigDecimal amountDue) { }
