package com.littlehorizons.erp.model;

import java.time.LocalDate;

/** grade is the numeric level (0 = Preescolar) of the latest enrollment, or -1 when there is none. */
public record Student(int id, String fullName, String documentType, String documentId, LocalDate birthDate,
                      String address, String status, int grade, int enrollmentYear) {
    public String gradeLabel() {
        return grade < 0 ? "Sin matricula" : grade == 0 ? "Preescolar" : grade + "°";
    }
}
