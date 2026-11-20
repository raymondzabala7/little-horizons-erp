package com.littlehorizons.erp.model;

/** Four-part Colombian name; middle and second last name are optional (null). */
public record PersonName(String first, String middle, String last, String secondLast) { }
