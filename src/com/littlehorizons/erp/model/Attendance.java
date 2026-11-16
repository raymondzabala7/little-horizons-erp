package com.littlehorizons.erp.model;

import java.time.LocalDate;

public record Attendance(int id, int studentId, String studentName, int subjectId, LocalDate date, String status) { }
