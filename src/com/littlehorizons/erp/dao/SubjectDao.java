package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectDao {
    List<Subject> findAll();
    List<Subject> findByGrade(int gradeLevel);
    Optional<Subject> findById(int id);
    int insert(String code, String name, int gradeLevel);
    boolean isEnrolled(int studentId, int subjectId, int year);
    boolean isEnrolledInPeriod(int studentId, int subjectId, int year, String period);
    int enroll(int studentId, int subjectId, int year, String period);
    List<Subject> findEnrolledSubjects(int studentId, int year);
    List<Student> findEnrolledStudents(int subjectId, int year);
}
