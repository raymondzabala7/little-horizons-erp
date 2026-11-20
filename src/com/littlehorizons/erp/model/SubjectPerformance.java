package com.littlehorizons.erp.model;

import java.math.BigDecimal;

/** General average (one decimal) of every grade recorded for a subject. */
public record SubjectPerformance(int subjectId, String code, String name, BigDecimal average) { }
