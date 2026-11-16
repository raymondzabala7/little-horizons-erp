package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class StudentDaoImpl implements StudentDao {

    private static final String SELECT = "SELECT id_estudiante, nombre_completo, documento_identidad, fecha_nacimiento, direccion, "
            + "contacto_emergencia, id_acudiente, grado, anio_matricula, estado FROM estudiantes";
    private static final Jdbc.RowMapper<Student> MAPPER = rs -> new Student(rs.getInt("id_estudiante"),
            rs.getString("nombre_completo"), rs.getString("documento_identidad"),
            rs.getObject("fecha_nacimiento", LocalDate.class), rs.getString("direccion"),
            rs.getString("contacto_emergencia"), rs.getInt("id_acudiente"), rs.getInt("grado"),
            rs.getInt("anio_matricula"), rs.getString("estado"));

    @Override
    public List<Student> findAll() {
        return Jdbc.query(SELECT + " ORDER BY grado, nombre_completo", MAPPER);
    }

    @Override
    public Optional<Student> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE id_estudiante = ?", MAPPER, id);
    }

    @Override
    public Optional<Student> findByDocument(String documentId) {
        return Jdbc.queryOne(SELECT + " WHERE documento_identidad = ?", MAPPER, documentId);
    }

    @Override
    public List<Student> findByGuardian(int guardianId) {
        return Jdbc.query(SELECT + " WHERE id_acudiente = ? ORDER BY nombre_completo", MAPPER, guardianId);
    }

    @Override
    public List<Student> searchByName(String text) {
        return Jdbc.query(SELECT + " WHERE nombre_completo LIKE ? ORDER BY nombre_completo", MAPPER, "%" + text + "%");
    }

    @Override
    public int insert(Student s) {
        return Jdbc.insert("INSERT INTO estudiantes (nombre_completo, documento_identidad, fecha_nacimiento, direccion, "
                        + "contacto_emergencia, id_acudiente, grado, anio_matricula, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                s.fullName(), s.documentId(), s.birthDate(), s.address(), s.emergencyContact(), s.guardianId(),
                s.grade(), s.enrollmentYear(), s.status());
    }

    @Override
    public int updateStatus(int studentId, String status) {
        return Jdbc.update("UPDATE estudiantes SET estado = ? WHERE id_estudiante = ?", status, studentId);
    }
}
