package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.GuardianDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.exception.BusinessRuleException;
import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.GuardianForm;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.StudentForm;

import java.time.LocalDate;
import java.util.List;

/** Student and guardian management. */
public class StudentService {

    private final StudentDao studentDao;
    private final GuardianDao guardianDao;

    public StudentService(StudentDao studentDao, GuardianDao guardianDao) {
        this.studentDao = studentDao;
        this.guardianDao = guardianDao;
    }

    public int registerGuardian(GuardianForm form) {
        if (guardianDao.findByDocument(form.documentId()).isPresent()) {
            throw new BusinessRuleException("Ya existe un acudiente con documento " + form.documentId() + ".");
        }
        return guardianDao.insert(form);
    }

    public int registerStudent(StudentForm form) {
        if (studentDao.findByDocument(form.documentId()).isPresent()) {
            throw new BusinessRuleException("Ya existe un estudiante con documento " + form.documentId() + ".");
        }
        getGuardian(form.guardianId());
        if (!form.birthDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException("La fecha de nacimiento debe ser anterior a hoy.");
        }
        return studentDao.insert(form);
    }

    public List<Student> listStudents() {
        return studentDao.findAll();
    }

    public List<Student> search(String text) {
        return studentDao.searchByName(text);
    }

    public Student getStudent(int id) {
        return studentDao.findById(id).orElseThrow(() -> new BusinessRuleException("Estudiante " + id + " no encontrado."));
    }

    public Guardian getGuardian(int id) {
        return guardianDao.findById(id).orElseThrow(() -> new BusinessRuleException("Acudiente " + id + " no encontrado."));
    }

    public Guardian principalGuardianOf(int studentId) {
        return guardianDao.findPrincipalOfStudent(studentId)
                .orElseThrow(() -> new BusinessRuleException("El estudiante no tiene acudiente asociado."));
    }

    public List<Guardian> listGuardians() {
        return guardianDao.findAll();
    }

    public List<Student> studentsOfGuardian(int guardianId) {
        getGuardian(guardianId);
        return studentDao.findByGuardian(guardianId);
    }

    public void changeStatus(int studentId, String status) {
        getStudent(studentId);
        if (!Catalog.STUDENT_STATUSES.contains(status)) {
            throw new BusinessRuleException("Estado invalido: " + status);
        }
        studentDao.updateStatus(studentId, status);
    }
}
