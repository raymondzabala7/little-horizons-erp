package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeachingAssignment;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TeacherDao {
    List<Teacher> findAll();
    Optional<Teacher> findById(int id);
    Optional<Teacher> findByDocument(String documentId);
    int insert(Teacher teacher);
    List<TeachingAssignment> findAssignments(int teacherId);
    boolean hasScheduleConflict(int teacherId, String day, LocalTime start, LocalTime end);
    int insertAssignment(int teacherId, int subjectId, String day, LocalTime start, LocalTime end);
}
