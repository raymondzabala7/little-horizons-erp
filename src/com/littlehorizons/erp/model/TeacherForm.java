package com.littlehorizons.erp.model;

public record TeacherForm(PersonName name, String documentType, String documentId, String email, String phone,
                          String specialization) { }
