package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.AcademicRecord;

import java.math.BigDecimal;
import java.util.List;

public class AcademicRecordDaoImpl implements AcademicRecordDao {

    private static final String SELECT = "SELECT r.id_registro, r.id_estudiante, r.id_materia, m.nombre, r.periodo, "
            + "r.anio_academico, r.nota FROM registros_academicos r JOIN materias m ON m.id_materia = r.id_materia";
    private static final Jdbc.RowMapper<AcademicRecord> MAPPER = rs -> new AcademicRecord(rs.getInt("id_registro"),
            rs.getInt("id_estudiante"), rs.getInt("id_materia"), rs.getString("nombre"), rs.getString("periodo"),
            rs.getInt("anio_academico"), rs.getBigDecimal("nota"));

    @Override
    public void save(int studentId, int subjectId, String period, int year, BigDecimal grade) {
        Jdbc.update("INSERT INTO registros_academicos (id_estudiante, id_materia, periodo, anio_academico, nota) "
                + "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE nota = VALUES(nota)",
                studentId, subjectId, period, year, grade);
    }

    @Override
    public List<AcademicRecord> findByStudent(int studentId) {
        return Jdbc.query(SELECT + " WHERE r.id_estudiante = ? ORDER BY r.anio_academico, m.nombre, r.periodo", MAPPER, studentId);
    }

    @Override
    public List<AcademicRecord> findBySubject(int subjectId, int year) {
        return Jdbc.query(SELECT + " WHERE r.id_materia = ? AND r.anio_academico = ?", MAPPER, subjectId, year);
    }
}
