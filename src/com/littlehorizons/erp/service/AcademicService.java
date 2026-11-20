package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.AcademicRecordDao;
import com.littlehorizons.erp.dao.AttendanceDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.dao.SubjectDao;
import com.littlehorizons.erp.dao.TeacherDao;
import com.littlehorizons.erp.exception.BusinessRuleException;
import com.littlehorizons.erp.model.AbsenceSummary;
import com.littlehorizons.erp.model.AcademicRecord;
import com.littlehorizons.erp.model.Attendance;
import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.Subject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Subjects, enrollments, grades, averages and attendance.
 * Averages use periods I-IV; the "Final" record is a summary and is excluded to avoid double counting.
 */
public class AcademicService {

    private static final int ABSENCE_THRESHOLD = 3;

    private final SubjectDao subjectDao;
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;
    private final AcademicRecordDao recordDao;
    private final AttendanceDao attendanceDao;

    public AcademicService(SubjectDao subjectDao, StudentDao studentDao, TeacherDao teacherDao,
                           AcademicRecordDao recordDao, AttendanceDao attendanceDao) {
        this.subjectDao = subjectDao;
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
        this.recordDao = recordDao;
        this.attendanceDao = attendanceDao;
    }

    // ---------- subjects and enrollment ----------

    public List<Subject> listSubjects() {
        return subjectDao.findAll();
    }

    public int createSubject(String code, String name, int gradeLevel) {
        return subjectDao.insert(code, name, gradeLevel);
    }

    public Subject getSubject(int id) {
        return subjectDao.findById(id).orElseThrow(() -> new BusinessRuleException("Materia " + id + " no encontrada."));
    }

    private Student getActiveStudent(int studentId) {
        Student student = studentDao.findById(studentId)
                .orElseThrow(() -> new BusinessRuleException("Estudiante " + studentId + " no encontrado."));
        if (!"Activo".equals(student.status())) {
            throw new BusinessRuleException("El estudiante no esta activo (estado: " + student.status() + ").");
        }
        if (student.grade() < 0) {
            throw new BusinessRuleException("El estudiante no tiene matricula en ningun grado.");
        }
        return student;
    }

    public void enroll(int studentId, int subjectId, int year, String period) {
        Student student = getActiveStudent(studentId);
        Subject subject = getSubject(subjectId);
        if (!Catalog.ENROLLMENT_PERIODS.contains(period)) {
            throw new BusinessRuleException("Periodo invalido para inscripcion: " + period);
        }
        if (subject.grade() != student.grade()) {
            throw new BusinessRuleException("La materia es de grado " + subject.grade() + " y el estudiante esta en " + student.gradeLabel() + ".");
        }
        if (subjectDao.isEnrolledInPeriod(studentId, subjectId, year, period)) {
            throw new BusinessRuleException("El estudiante ya esta inscrito en esa materia para el periodo " + period + " de " + year + ".");
        }
        subjectDao.enroll(studentId, subjectId, year, period);
    }

    /** Enrolls the student in every subject of their grade for the period. Returns how many were added. */
    public int enrollAllOfGrade(int studentId, int year, String period) {
        Student student = getActiveStudent(studentId);
        List<Subject> pending = subjectDao.findByGrade(student.grade()).stream()
                .filter(s -> !subjectDao.isEnrolledInPeriod(studentId, s.id(), year, period))
                .toList();
        pending.forEach(s -> subjectDao.enroll(studentId, s.id(), year, period));
        return pending.size();
    }

    public List<Subject> enrolledSubjects(int studentId, int year) {
        return subjectDao.findEnrolledSubjects(studentId, year);
    }

    public List<Student> enrolledStudents(int subjectId, int year) {
        return subjectDao.findEnrolledStudents(subjectId, year);
    }

    // ---------- grades ----------

    public void recordGrade(int studentId, int subjectId, int teacherId, String period, int year, BigDecimal grade) {
        if (!Catalog.PERIODS.contains(period)) {
            throw new BusinessRuleException("Periodo invalido: " + period);
        }
        if (grade.compareTo(BigDecimal.ZERO) < 0 || grade.compareTo(new BigDecimal("5.0")) > 0 || grade.scale() > 1) {
            throw new BusinessRuleException("La nota debe estar entre 0.0 y 5.0 con un decimal.");
        }
        teacherDao.findById(teacherId).orElseThrow(() -> new BusinessRuleException("Profesor " + teacherId + " no encontrado."));
        if (!subjectDao.isEnrolled(studentId, subjectId, year)) {
            throw new BusinessRuleException("El estudiante no esta inscrito en esa materia para " + year + ".");
        }
        recordDao.save(studentId, subjectId, teacherId, period, year, grade);
    }

    public List<AcademicRecord> history(int studentId) {
        return recordDao.findByStudent(studentId);
    }

    /** Average per subject for one student and year (periods I-IV). */
    public Map<String, Double> subjectAverages(int studentId, int year) {
        return recordDao.findByStudent(studentId).stream()
                .filter(r -> r.academicYear() == year && !"Final".equals(r.period()))
                .collect(Collectors.groupingBy(AcademicRecord::subjectName, TreeMap::new,
                        Collectors.averagingDouble(r -> r.grade().doubleValue())));
    }

    /** General average of a student in one period. */
    public OptionalDouble periodAverage(int studentId, String period, int year) {
        return recordDao.findByStudent(studentId).stream()
                .filter(r -> r.academicYear() == year && r.period().equals(period))
                .mapToDouble(r -> r.grade().doubleValue())
                .average();
    }

    /** General average of a student across the year (periods I-IV). */
    public OptionalDouble yearAverage(int studentId, int year) {
        return recordDao.findByStudent(studentId).stream()
                .filter(r -> r.academicYear() == year && !"Final".equals(r.period()))
                .mapToDouble(r -> r.grade().doubleValue())
                .average();
    }

    /** Course average for a subject in a year. */
    public OptionalDouble courseAverage(int subjectId, int year) {
        return recordDao.findBySubject(subjectId, year).stream()
                .filter(r -> !"Final".equals(r.period()))
                .mapToDouble(r -> r.grade().doubleValue())
                .average();
    }

    // ---------- attendance ----------

    public void markAttendance(int studentId, int subjectId, LocalDate date, String status) {
        if (!Catalog.ATTENDANCE_STATUSES.contains(status)) {
            throw new BusinessRuleException("Estado de asistencia invalido: " + status);
        }
        if (date.isAfter(LocalDate.now())) {
            throw new BusinessRuleException("No se puede registrar asistencia en una fecha futura.");
        }
        if (!subjectDao.isEnrolled(studentId, subjectId, date.getYear())) {
            throw new BusinessRuleException("El estudiante no esta inscrito en esa materia para " + date.getYear() + ".");
        }
        attendanceDao.save(studentId, subjectId, date, status);
    }

    public List<Attendance> attendanceOf(int studentId) {
        return attendanceDao.findByStudent(studentId);
    }

    /** Students with more than 3 absences in the 7 days starting at weekStart. */
    public List<AbsenceSummary> frequentAbsences(LocalDate weekStart) {
        return attendanceDao.findBetween(weekStart, weekStart.plusDays(6)).stream()
                .filter(a -> "Ausente".equals(a.status()))
                .collect(Collectors.groupingBy(Attendance::studentId))
                .values().stream()
                .filter(list -> list.size() > ABSENCE_THRESHOLD)
                .map(list -> new AbsenceSummary(list.get(0).studentId(), list.get(0).studentName(), list.size()))
                .sorted((a, b) -> Long.compare(b.absences(), a.absences()))
                .toList();
    }
}
