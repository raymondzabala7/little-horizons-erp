package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.service.StudentService;
import java.util.List;

public class StudentController {
    private final StudentService studentService = new StudentService();

    public boolean createStudent(Student student) {
        return studentService.registerStudent(student);
    }

    public List<Student> listStudents() {
        return studentService.getAllStudents();
    }
}
