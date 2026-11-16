package com.littlehorizons.erp.model;

import java.time.LocalTime;

public record TeachingAssignment(int id, int teacherId, String teacherName, int subjectId, String subjectName,
                                 String day, LocalTime start, LocalTime end) { }
