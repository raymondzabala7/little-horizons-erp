package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.StudentDAO;
import com.littlehorizons.erp.model.Student;
import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO = new StudentDAO();

    public boolean registerStudent(Student student) {
        return studentDAO.insert(student);
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }
}
