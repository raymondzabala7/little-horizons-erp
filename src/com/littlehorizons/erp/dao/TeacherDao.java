package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherForm;
import com.littlehorizons.erp.model.TeachingAssignment;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TeacherDao {
    List<Teacher> findAll();
    Optional<Teacher> findById(int id);
    Optional<Teacher> findByDocument(String documentId);
    int insert(TeacherForm form);
    List<TeachingAssignment> findAssignments(int teacherId);
    Optional<Integer> findAssignmentId(int teacherId, int subjectId, int year, String section);
    int insertAssignment(int teacherId, int subjectId, int year, String section);
    boolean hasScheduleConflict(int teacherId, int year, String day, LocalTime start, LocalTime end);
    int insertSchedule(int assignmentId, String day, LocalTime start, LocalTime end);
}
