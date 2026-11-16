package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Attendance;

import java.time.LocalDate;
import java.util.List;

public class AttendanceDaoImpl implements AttendanceDao {

    private static final String SELECT = "SELECT a.id_asistencia, a.id_estudiante, e.nombre_completo, a.id_materia, a.fecha, a.estado "
            + "FROM asistencias a JOIN estudiantes e ON e.id_estudiante = a.id_estudiante";
    private static final Jdbc.RowMapper<Attendance> MAPPER = rs -> new Attendance(rs.getInt("id_asistencia"),
            rs.getInt("id_estudiante"), rs.getString("nombre_completo"), rs.getInt("id_materia"),
            rs.getObject("fecha", LocalDate.class), rs.getString("estado"));

    @Override
    public void save(int studentId, int subjectId, LocalDate date, String status) {
        Jdbc.update("INSERT INTO asistencias (id_estudiante, id_materia, fecha, estado) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE estado = VALUES(estado)", studentId, subjectId, date, status);
    }

    @Override
    public List<Attendance> findBetween(LocalDate from, LocalDate to) {
        return Jdbc.query(SELECT + " WHERE a.fecha BETWEEN ? AND ? ORDER BY a.fecha", MAPPER, from, to);
    }

    @Override
    public List<Attendance> findByStudent(int studentId) {
        return Jdbc.query(SELECT + " WHERE a.id_estudiante = ? ORDER BY a.fecha DESC", MAPPER, studentId);
    }
}
