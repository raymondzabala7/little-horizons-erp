package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.Subject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SubjectDaoImpl implements SubjectDao {

    private static final String SELECT = "SELECT m.id_materia, m.codigo_materia, m.nombre_materia, g.nivel_numerico FROM materias m "
            + "JOIN grados g ON g.id_grado = m.id_grado";
    private static final Jdbc.RowMapper<Subject> MAPPER = rs -> new Subject(rs.getInt("id_materia"),
            rs.getString("codigo_materia"), rs.getString("nombre_materia"), rs.getInt("nivel_numerico"));

    @Override
    public List<Subject> findAll() {
        return Jdbc.query(SELECT + " ORDER BY g.nivel_numerico, m.nombre_materia", MAPPER);
    }

    @Override
    public List<Subject> findByGrade(int gradeLevel) {
        return Jdbc.query(SELECT + " WHERE g.nivel_numerico = ? ORDER BY m.nombre_materia", MAPPER, gradeLevel);
    }

    @Override
    public Optional<Subject> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE m.id_materia = ?", MAPPER, id);
    }

    @Override
    public int insert(String code, String name, int gradeLevel) {
        return Jdbc.insert("INSERT INTO materias (codigo_materia, nombre_materia, id_grado, esta_activa) "
                + "VALUES (?, ?, (SELECT id_grado FROM grados WHERE nivel_numerico = ?), 1)", code, name, gradeLevel);
    }

    @Override
    public boolean isEnrolled(int studentId, int subjectId, int year) {
        return !Jdbc.query("SELECT 1 AS x FROM inscripciones_materia WHERE id_estudiante = ? AND id_materia = ? "
                + "AND `año_academico` = ? AND estado <> 'Retirada'", rs -> rs.getInt("x"), studentId, subjectId, year).isEmpty();
    }

    @Override
    public boolean isEnrolledInPeriod(int studentId, int subjectId, int year, String period) {
        return !Jdbc.query("SELECT 1 AS x FROM inscripciones_materia WHERE id_estudiante = ? AND id_materia = ? "
                + "AND `año_academico` = ? AND periodo_academico = ?", rs -> rs.getInt("x"), studentId, subjectId, year, period).isEmpty();
    }

    @Override
    public int enroll(int studentId, int subjectId, int year, String period) {
        return Jdbc.insert("INSERT INTO inscripciones_materia (id_estudiante, id_materia, `año_academico`, periodo_academico, "
                + "fecha_inscripcion, estado) VALUES (?, ?, ?, ?, ?, 'Activa')", studentId, subjectId, year, period, LocalDate.now());
    }

    @Override
    public List<Subject> findEnrolledSubjects(int studentId, int year) {
        return Jdbc.query(SELECT + " WHERE m.id_materia IN (SELECT id_materia FROM inscripciones_materia WHERE id_estudiante = ? "
                + "AND `año_academico` = ? AND estado <> 'Retirada') ORDER BY m.nombre_materia", MAPPER, studentId, year);
    }

    @Override
    public List<Student> findEnrolledStudents(int subjectId, int year) {
        return Jdbc.query(StudentDaoImpl.SELECT + " WHERE e.id_estudiante IN (SELECT id_estudiante FROM inscripciones_materia "
                + "WHERE id_materia = ? AND `año_academico` = ? AND estado <> 'Retirada') ORDER BY e.primer_apellido",
                StudentDaoImpl.MAPPER, subjectId, year);
    }
}
