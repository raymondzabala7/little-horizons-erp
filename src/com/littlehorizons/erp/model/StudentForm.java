package com.littlehorizons.erp.model;

import java.time.LocalDate;

public record StudentForm(PersonName name, String documentType, String documentId, LocalDate birthDate, String gender,
                          String address, int guardianId, int gradeLevel, int enrollmentYear) { }
