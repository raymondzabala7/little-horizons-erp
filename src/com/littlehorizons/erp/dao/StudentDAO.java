package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentDao {
    List<Student> findAll();
    Optional<Student> findById(int id);
    Optional<Student> findByDocument(String documentId);
    List<Student> findByGuardian(int guardianId);
    List<Student> searchByName(String text);
    int insert(Student student);
    int updateStatus(int studentId, String status);
}
