package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.StudentForm;

import java.util.List;
import java.util.Optional;

public interface StudentDao {
    List<Student> findAll();
    Optional<Student> findById(int id);
    Optional<Student> findByDocument(String documentId);
    List<Student> findByGuardian(int guardianId);
    List<Student> searchByName(String text);
    /** Creates the student, links the guardian as principal and creates the enrollment, all in one transaction. */
    int insert(StudentForm form);
    int updateStatus(int studentId, String status);
}
