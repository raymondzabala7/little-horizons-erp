package com.littlehorizons.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Payment(int id, int studentId, String studentName, int conceptId, String conceptName,
                      BigDecimal amount, LocalDate dueDate, LocalDate paymentDate, String method,
                      String receiptNumber, String status, boolean taxable) {
    public boolean isPaid() {
        return "Pagado".equals(status);
    }
}
