package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.SubjectDao;
import com.littlehorizons.erp.dao.TeacherDao;
import com.littlehorizons.erp.exception.BusinessRuleException;
import com.littlehorizons.erp.model.Teacher;
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

    public int register(Teacher teacher) {
        if (teacherDao.findByDocument(teacher.documentId()).isPresent()) {
            throw new BusinessRuleException("Ya existe un profesor con documento " + teacher.documentId() + ".");
        }
        return teacherDao.insert(teacher);
    }

    public List<Teacher> list() {
        return teacherDao.findAll();
    }

    public Teacher get(int id) {
        return teacherDao.findById(id).orElseThrow(() -> new BusinessRuleException("Profesor " + id + " no encontrado."));
    }

    public int assignSubject(int teacherId, int subjectId, String day, LocalTime start, LocalTime end) {
        get(teacherId);
        subjectDao.findById(subjectId).orElseThrow(() -> new BusinessRuleException("Materia " + subjectId + " no encontrado."));
        if (!end.isAfter(start)) {
            throw new BusinessRuleException("La hora de fin debe ser posterior a la de inicio.");
        }
        if (teacherDao.hasScheduleConflict(teacherId, day, start, end)) {
            throw new BusinessRuleException("Conflicto de horario: el profesor ya tiene una clase que se cruza el " + day + ".");
        }
        return teacherDao.insertAssignment(teacherId, subjectId, day, start, end);
    }

    public List<TeachingAssignment> assignments(int teacherId) {
        get(teacherId);
        return teacherDao.findAssignments(teacherId);
    }
}
