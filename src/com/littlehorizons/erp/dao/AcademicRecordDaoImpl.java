package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.AcademicRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AcademicRecordDaoImpl implements AcademicRecordDao {

    private static final String SELECT = "SELECT r.id_registro, r.id_estudiante, r.id_materia, m.nombre_materia, r.periodo_academico, "
            + "r.`año_academico` AS anio, r.valor_nota FROM registros_academicos r JOIN materias m ON m.id_materia = r.id_materia";
    private static final Jdbc.RowMapper<AcademicRecord> MAPPER = rs -> new AcademicRecord(rs.getInt("id_registro"),
            rs.getInt("id_estudiante"), rs.getInt("id_materia"), rs.getString("nombre_materia"), rs.getString("periodo_academico"),
            rs.getInt("anio"), rs.getBigDecimal("valor_nota"));

    @Override
    public void save(int studentId, int subjectId, int teacherId, String period, int year, BigDecimal grade) {
        var existing = Jdbc.queryOne("SELECT id_registro FROM registros_academicos WHERE id_estudiante = ? AND id_materia = ? "
                + "AND periodo_academico = ? AND `año_academico` = ?", rs -> rs.getInt(1), studentId, subjectId, period, year);
        if (existing.isPresent()) {
            Jdbc.update("UPDATE registros_academicos SET valor_nota = ?, id_profesor = ?, fecha_registro = ? WHERE id_registro = ?",
                    grade, teacherId, LocalDate.now(), existing.get());
        } else {
            Jdbc.insert("INSERT INTO registros_academicos (id_estudiante, id_materia, id_profesor, `año_academico`, periodo_academico, "
                    + "valor_nota, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?)", studentId, subjectId, teacherId, year, period, grade, LocalDate.now());
        }
    }

    @Override
    public List<AcademicRecord> findByStudent(int studentId) {
        return Jdbc.query(SELECT + " WHERE r.id_estudiante = ? ORDER BY r.`año_academico`, m.nombre_materia, r.periodo_academico", MAPPER, studentId);
    }

    @Override
    public List<AcademicRecord> findBySubject(int subjectId, int year) {
        return Jdbc.query(SELECT + " WHERE r.id_materia = ? AND r.`año_academico` = ?", MAPPER, subjectId, year);
    }
}
