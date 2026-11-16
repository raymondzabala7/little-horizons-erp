package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectDao {
    List<Subject> findAll();
    List<Subject> findByGrade(int grade);
    Optional<Subject> findById(int id);
    int insert(String name, int grade);
    boolean isEnrolled(int studentId, int subjectId, int year);
    int enroll(int studentId, int subjectId, int year);
    List<Subject> findEnrolledSubjects(int studentId, int year);
    List<Student> findEnrolledStudents(int subjectId, int year);
}
