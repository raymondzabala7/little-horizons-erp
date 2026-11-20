package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.SubjectDao;
import com.littlehorizons.erp.dao.TeacherDao;
import com.littlehorizons.erp.exception.BusinessRuleException;
import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherForm;
import com.littlehorizons.erp.model.TeachingAssignment;

import java.time.LocalTime;
import java.util.List;

/** Teacher directory and teaching load assignment. */
public class TeacherService {

    private final TeacherDao teacherDao;
    private final SubjectDao subjectDao;

    public TeacherService(TeacherDao teacherDao, SubjectDao subjectDao) {
        this.teacherDao = teacherDao;
        this.subjectDao = subjectDao;
    }

    public int register(TeacherForm form) {
        if (teacherDao.findByDocument(form.documentId()).isPresent()) {
            throw new BusinessRuleException("Ya existe un profesor con documento " + form.documentId() + ".");
        }
        return teacherDao.insert(form);
    }

    public List<Teacher> list() {
        return teacherDao.findAll();
    }

    public Teacher get(int id) {
        return teacherDao.findById(id).orElseThrow(() -> new BusinessRuleException("Profesor " + id + " no encontrado."));
    }

    /** Assigns the subject (reusing an existing assignment) and registers its weekly schedule slot. */
    public int assignSubject(int teacherId, int subjectId, int year, String section, String day, LocalTime start, LocalTime end) {
        get(teacherId);
        subjectDao.findById(subjectId).orElseThrow(() -> new BusinessRuleException("Materia " + subjectId + " no encontrada."));
        if (!end.isAfter(start)) {
            throw new BusinessRuleException("La hora de fin debe ser posterior a la de inicio.");
        }
        if (teacherDao.hasScheduleConflict(teacherId, year, day, start, end)) {
            throw new BusinessRuleException("Conflicto de horario: el profesor ya tiene una clase que se cruza el " + day + ".");
        }
        int assignmentId = teacherDao.findAssignmentId(teacherId, subjectId, year, section)
                .orElseGet(() -> teacherDao.insertAssignment(teacherId, subjectId, year, section));
        return teacherDao.insertSchedule(assignmentId, day, start, end);
    }

    public List<TeachingAssignment> assignments(int teacherId) {
        get(teacherId);
        return teacherDao.findAssignments(teacherId);
    }
}
