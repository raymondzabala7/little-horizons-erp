package com.littlehorizons.erp.model;

import java.time.LocalTime;

/** day, start and end are null while the assignment has no schedule yet. */
public record TeachingAssignment(int id, int teacherId, String teacherName, int subjectId, String subjectName,
                                 int year, String section, String day, LocalTime start, LocalTime end) { }
