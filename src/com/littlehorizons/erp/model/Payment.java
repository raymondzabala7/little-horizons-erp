package com.littlehorizons.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Payment(int id, int studentId, String studentName, int conceptId, String conceptCode, String conceptName,
                      BigDecimal amount, LocalDate paymentDate, String method, String receiptNumber,
                      Integer year, String period, Integer month) { }
