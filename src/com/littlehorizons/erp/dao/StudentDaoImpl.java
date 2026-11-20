package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.StudentForm;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class StudentDaoImpl implements StudentDao {

    /** Student plus the grade of the latest enrollment. Shared with SubjectDaoImpl. */
    static final String SELECT = "SELECT e.id_estudiante, e.tipo_documento, e.numero_documento, "
            + "CONCAT_WS(' ', e.primer_nombre, e.segundo_nombre, e.primer_apellido, e.segundo_apellido) AS nombre, "
            + "e.fecha_nacimiento, e.direccion, e.estado, g.nivel_numerico, m.`año_academico` AS anio_matricula FROM estudiantes e "
            + "LEFT JOIN matriculas m ON m.id_matricula = (SELECT m2.id_matricula FROM matriculas m2 WHERE m2.id_estudiante = e.id_estudiante "
            + "ORDER BY m2.`año_academico` DESC, m2.id_matricula DESC LIMIT 1) LEFT JOIN grados g ON g.id_grado = m.id_grado";

    static final Jdbc.RowMapper<Student> MAPPER = rs -> {
        int level = rs.getInt("nivel_numerico");
        if (rs.wasNull()) {
            level = -1;
        }
        return new Student(rs.getInt("id_estudiante"), rs.getString("nombre"), rs.getString("tipo_documento"),
                rs.getString("numero_documento"), rs.getObject("fecha_nacimiento", LocalDate.class), rs.getString("direccion"),
                rs.getString("estado"), level, rs.getInt("anio_matricula"));
    };

    @Override
    public List<Student> findAll() {
        return Jdbc.query(SELECT + " ORDER BY g.nivel_numerico, e.primer_apellido, e.primer_nombre", MAPPER);
    }

    @Override
    public Optional<Student> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE e.id_estudiante = ?", MAPPER, id);
    }

    @Override
    public Optional<Student> findByDocument(String documentId) {
        return Jdbc.queryOne(SELECT + " WHERE e.numero_documento = ?", MAPPER, documentId);
    }

    @Override
    public List<Student> findByGuardian(int guardianId) {
        return Jdbc.query(SELECT + " WHERE e.id_estudiante IN (SELECT id_estudiante FROM estudiante_acudiente WHERE id_acudiente = ?) "
                + "ORDER BY e.primer_nombre", MAPPER, guardianId);
    }

    @Override
    public List<Student> searchByName(String text) {
        String like = "%" + text + "%";
        return Jdbc.query(SELECT + " WHERE CONCAT_WS(' ', e.primer_nombre, e.segundo_nombre, e.primer_apellido, e.segundo_apellido) LIKE ? "
                + "ORDER BY e.primer_apellido", MAPPER, like);
    }

    @Override
    public int insert(StudentForm f) {
        return Jdbc.transaction(() -> {
            int id = Jdbc.insert("INSERT INTO estudiantes (tipo_documento, numero_documento, primer_nombre, segundo_nombre, primer_apellido, "
                            + "segundo_apellido, fecha_nacimiento, genero, direccion, fecha_ingreso, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Activo')",
                    f.documentType(), f.documentId(), f.name().first(), f.name().middle(), f.name().last(), f.name().secondLast(),
                    f.birthDate(), f.gender(), f.address(), LocalDate.now());
            Jdbc.update("INSERT INTO estudiante_acudiente (id_estudiante, id_acudiente, es_principal, es_contacto_emergencia) VALUES (?, ?, 1, 1)",
                    id, f.guardianId());
            Jdbc.update("INSERT INTO matriculas (id_estudiante, id_grado, `año_academico`, estado_matricula, fecha_matricula) "
                            + "VALUES (?, (SELECT id_grado FROM grados WHERE nivel_numerico = ?), ?, 'Activa', ?)",
                    id, f.gradeLevel(), f.enrollmentYear(), LocalDate.now());
            return id;
        });
    }

    @Override
    public int updateStatus(int studentId, String status) {
        return Jdbc.update("UPDATE estudiantes SET estado = ? WHERE id_estudiante = ?", status, studentId);
    }
}
