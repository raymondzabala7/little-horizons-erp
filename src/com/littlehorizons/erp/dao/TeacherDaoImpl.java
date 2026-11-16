package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeachingAssignment;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class TeacherDaoImpl implements TeacherDao {

    private static final String SELECT = "SELECT id_profesor, nombre_completo, documento_identidad, especializacion, telefono, correo FROM profesores";
    private static final Jdbc.RowMapper<Teacher> MAPPER = rs -> new Teacher(rs.getInt("id_profesor"),
            rs.getString("nombre_completo"), rs.getString("documento_identidad"), rs.getString("especializacion"),
            rs.getString("telefono"), rs.getString("correo"));
    private static final Jdbc.RowMapper<TeachingAssignment> ASSIGNMENT_MAPPER = rs -> new TeachingAssignment(
            rs.getInt("id_asignacion"), rs.getInt("id_profesor"), rs.getString("nombre_completo"),
            rs.getInt("id_materia"), rs.getString("materia"), rs.getString("dia_semana"),
            rs.getObject("hora_inicio", LocalTime.class), rs.getObject("hora_fin", LocalTime.class));

    @Override
    public List<Teacher> findAll() {
        return Jdbc.query(SELECT + " ORDER BY nombre_completo", MAPPER);
    }

    @Override
    public Optional<Teacher> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE id_profesor = ?", MAPPER, id);
    }

    @Override
    public Optional<Teacher> findByDocument(String documentId) {
        return Jdbc.queryOne(SELECT + " WHERE documento_identidad = ?", MAPPER, documentId);
    }

    @Override
    public int insert(Teacher t) {
        return Jdbc.insert("INSERT INTO profesores (nombre_completo, documento_identidad, especializacion, telefono, correo) VALUES (?, ?, ?, ?, ?)",
                t.fullName(), t.documentId(), t.specialization(), t.phone(), t.email());
    }

    @Override
    public List<TeachingAssignment> findAssignments(int teacherId) {
        return Jdbc.query("SELECT a.id_asignacion, a.id_profesor, p.nombre_completo, a.id_materia, "
                + "CONCAT(m.nombre, ' (', m.grado, ')') AS materia, a.dia_semana, a.hora_inicio, a.hora_fin "
                + "FROM asignaciones_profesor a JOIN profesores p ON p.id_profesor = a.id_profesor "
                + "JOIN materias m ON m.id_materia = a.id_materia WHERE a.id_profesor = ? "
                + "ORDER BY FIELD(a.dia_semana, 'Lunes','Martes','Miercoles','Jueves','Viernes'), a.hora_inicio",
                ASSIGNMENT_MAPPER, teacherId);
    }

    @Override
    public boolean hasScheduleConflict(int teacherId, String day, LocalTime start, LocalTime end) {
        return !Jdbc.query("SELECT 1 AS x FROM asignaciones_profesor WHERE id_profesor = ? AND dia_semana = ? "
                + "AND hora_inicio < ? AND hora_fin > ?", rs -> rs.getInt("x"), teacherId, day, end, start).isEmpty();
    }

    @Override
    public int insertAssignment(int teacherId, int subjectId, String day, LocalTime start, LocalTime end) {
        return Jdbc.insert("INSERT INTO asignaciones_profesor (id_profesor, id_materia, dia_semana, hora_inicio, hora_fin) VALUES (?, ?, ?, ?, ?)",
                teacherId, subjectId, day, start, end);
    }
}
