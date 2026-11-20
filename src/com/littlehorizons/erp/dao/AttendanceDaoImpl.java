package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Attendance;

import java.time.LocalDate;
import java.util.List;

public class AttendanceDaoImpl implements AttendanceDao {

    private static final String SELECT = "SELECT a.id_asistencia, a.id_estudiante, "
            + "CONCAT_WS(' ', e.primer_nombre, e.segundo_nombre, e.primer_apellido, e.segundo_apellido) AS nombre, "
            + "a.id_materia, a.fecha_asistencia, a.estado_asistencia FROM asistencias a JOIN estudiantes e ON e.id_estudiante = a.id_estudiante";
    private static final Jdbc.RowMapper<Attendance> MAPPER = rs -> new Attendance(rs.getInt("id_asistencia"),
            rs.getInt("id_estudiante"), rs.getString("nombre"), rs.getInt("id_materia"),
            rs.getObject("fecha_asistencia", LocalDate.class), rs.getString("estado_asistencia"));

    @Override
    public void save(int studentId, int subjectId, LocalDate date, String status) {
        var existing = Jdbc.queryOne("SELECT id_asistencia FROM asistencias WHERE id_estudiante = ? AND id_materia = ? AND fecha_asistencia = ?",
                rs -> rs.getInt(1), studentId, subjectId, date);
        if (existing.isPresent()) {
            Jdbc.update("UPDATE asistencias SET estado_asistencia = ? WHERE id_asistencia = ?", status, existing.get());
        } else {
            Jdbc.insert("INSERT INTO asistencias (id_estudiante, id_materia, fecha_asistencia, estado_asistencia) VALUES (?, ?, ?, ?)",
                    studentId, subjectId, date, status);
        }
    }

    @Override
    public List<Attendance> findBetween(LocalDate from, LocalDate to) {
        return Jdbc.query(SELECT + " WHERE a.fecha_asistencia BETWEEN ? AND ? ORDER BY a.fecha_asistencia", MAPPER, from, to);
    }

    @Override
    public List<Attendance> findByStudent(int studentId) {
        return Jdbc.query(SELECT + " WHERE a.id_estudiante = ? ORDER BY a.fecha_asistencia DESC", MAPPER, studentId);
    }
}
