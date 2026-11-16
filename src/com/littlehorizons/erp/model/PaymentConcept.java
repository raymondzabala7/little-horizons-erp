package com.littlehorizons.erp.model;

import java.math.BigDecimal;

public record PaymentConcept(int id, String name, BigDecimal baseValue, boolean taxable) { }
