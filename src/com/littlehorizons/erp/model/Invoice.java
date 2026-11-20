package com.littlehorizons.erp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Invoice(int id, String number, int paymentId, LocalDateTime issuedAt, BigDecimal subtotal,
                      BigDecimal tax, BigDecimal total, String cufe, String filePath) { }
