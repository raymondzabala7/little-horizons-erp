package com.littlehorizons.erp.model;

import java.time.LocalDate;

public record Loan(int id, int resourceId, String resourceCode, String resourceName, String borrowerType,
                   int borrowerId, String borrowerName, LocalDate loanDate, LocalDate dueDate,
                   LocalDate returnDate, String status) {
    public static final String STUDENT = "Estudiante";
    public static final String TEACHER = "Profesor";
}
