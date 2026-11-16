package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.Subject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SubjectDaoImpl implements SubjectDao {

    private static final String SELECT = "SELECT id_materia, nombre, grado FROM materias";
    private static final Jdbc.RowMapper<Subject> MAPPER =
            rs -> new Subject(rs.getInt("id_materia"), rs.getString("nombre"), rs.getInt("grado"));

    @Override
    public List<Subject> findAll() {
        return Jdbc.query(SELECT + " ORDER BY grado, nombre", MAPPER);
    }

    @Override
    public List<Subject> findByGrade(int grade) {
        return Jdbc.query(SELECT + " WHERE grado = ? ORDER BY nombre", MAPPER, grade);
    }

    @Override
    public Optional<Subject> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE id_materia = ?", MAPPER, id);
    }

    @Override
    public int insert(String name, int grade) {
        return Jdbc.insert("INSERT INTO materias (nombre, grado) VALUES (?, ?)", name, grade);
    }

    @Override
    public boolean isEnrolled(int studentId, int subjectId, int year) {
        return !Jdbc.query("SELECT 1 AS x FROM inscripciones_materia WHERE id_estudiante = ? AND id_materia = ? AND anio_academico = ?",
                rs -> rs.getInt("x"), studentId, subjectId, year).isEmpty();
    }

    @Override
    public int enroll(int studentId, int subjectId, int year) {
        return Jdbc.insert("INSERT INTO inscripciones_materia (id_estudiante, id_materia, anio_academico) VALUES (?, ?, ?)",
                studentId, subjectId, year);
    }

    @Override
    public List<Subject> findEnrolledSubjects(int studentId, int year) {
        return Jdbc.query("SELECT m.id_materia, m.nombre, m.grado FROM materias m "
                + "JOIN inscripciones_materia i ON i.id_materia = m.id_materia "
                + "WHERE i.id_estudiante = ? AND i.anio_academico = ? ORDER BY m.nombre", MAPPER, studentId, year);
    }

    @Override
    public List<Student> findEnrolledStudents(int subjectId, int year) {
        return Jdbc.query("SELECT e.id_estudiante, e.nombre_completo, e.documento_identidad, e.fecha_nacimiento, e.direccion, "
                + "e.contacto_emergencia, e.id_acudiente, e.grado, e.anio_matricula, e.estado FROM estudiantes e "
                + "JOIN inscripciones_materia i ON i.id_estudiante = e.id_estudiante "
                + "WHERE i.id_materia = ? AND i.anio_academico = ? ORDER BY e.nombre_completo",
                rs -> new Student(rs.getInt("id_estudiante"), rs.getString("nombre_completo"),
                        rs.getString("documento_identidad"), rs.getObject("fecha_nacimiento", LocalDate.class),
                        rs.getString("direccion"), rs.getString("contacto_emergencia"), rs.getInt("id_acudiente"),
                        rs.getInt("grado"), rs.getInt("anio_matricula"), rs.getString("estado")),
                subjectId, year);
    }
}
