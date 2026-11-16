package com.littlehorizons.erp.model;

import java.time.LocalDate;

public record Student(int id, String fullName, String documentId, LocalDate birthDate, String address,
                      String emergencyContact, int guardianId, int grade, int enrollmentYear, String status) {

    /** Human readable grade: 0 is Preescolar. */
    public String gradeLabel() {
        return grade == 0 ? "Preescolar" : grade + "°";
    }
}
