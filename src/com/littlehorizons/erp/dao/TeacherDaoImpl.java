package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherForm;
import com.littlehorizons.erp.model.TeachingAssignment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class TeacherDaoImpl implements TeacherDao {

    private static final String NAME = "CONCAT_WS(' ', p.primer_nombre, p.segundo_nombre, p.primer_apellido, p.segundo_apellido)";
    private static final String SELECT = "SELECT p.id_profesor, " + NAME + " AS nombre, p.numero_documento, p.especializacion, "
            + "p.telefono, p.correo_electronico, p.estado FROM profesores p";
    private static final Jdbc.RowMapper<Teacher> MAPPER = rs -> new Teacher(rs.getInt("id_profesor"), rs.getString("nombre"),
            rs.getString("numero_documento"), rs.getString("especializacion"), rs.getString("telefono"),
            rs.getString("correo_electronico"), rs.getString("estado"));

    @Override
    public List<Teacher> findAll() {
        return Jdbc.query(SELECT + " ORDER BY p.primer_apellido, p.primer_nombre", MAPPER);
    }

    @Override
    public Optional<Teacher> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE p.id_profesor = ?", MAPPER, id);
    }

    @Override
    public Optional<Teacher> findByDocument(String documentId) {
        return Jdbc.queryOne(SELECT + " WHERE p.numero_documento = ?", MAPPER, documentId);
    }

    @Override
    public int insert(TeacherForm f) {
        return Jdbc.insert("INSERT INTO profesores (tipo_documento, numero_documento, primer_nombre, segundo_nombre, primer_apellido, "
                        + "segundo_apellido, correo_electronico, telefono, especializacion, fecha_contratacion, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Activo')",
                f.documentType(), f.documentId(), f.name().first(), f.name().middle(), f.name().last(), f.name().secondLast(),
                f.email(), f.phone(), f.specialization(), LocalDate.now());
    }

    @Override
    public List<TeachingAssignment> findAssignments(int teacherId) {
        return Jdbc.query("SELECT a.id_asignacion, a.id_profesor, " + NAME + " AS nombre, a.id_materia, m.nombre_materia, "
                + "a.`año_academico` AS anio, a.seccion, h.dia_semana, h.hora_inicio, h.hora_fin FROM asignaciones_profesor a "
                + "JOIN profesores p ON p.id_profesor = a.id_profesor JOIN materias m ON m.id_materia = a.id_materia "
                + "LEFT JOIN horarios_clase h ON h.id_asignacion = a.id_asignacion WHERE a.id_profesor = ? "
                + "ORDER BY a.`año_academico` DESC, FIELD(h.dia_semana, 'Lunes','Martes','Miércoles','Jueves','Viernes'), h.hora_inicio",
                rs -> new TeachingAssignment(rs.getInt("id_asignacion"), rs.getInt("id_profesor"), rs.getString("nombre"),
                        rs.getInt("id_materia"), rs.getString("nombre_materia"), rs.getInt("anio"), rs.getString("seccion"),
                        rs.getString("dia_semana"), rs.getObject("hora_inicio", LocalTime.class), rs.getObject("hora_fin", LocalTime.class)),
                teacherId);
    }

    @Override
    public Optional<Integer> findAssignmentId(int teacherId, int subjectId, int year, String section) {
        return Jdbc.queryOne("SELECT id_asignacion FROM asignaciones_profesor WHERE id_profesor = ? AND id_materia = ? "
                + "AND `año_academico` = ? AND (seccion <=> ?)", rs -> rs.getInt(1), teacherId, subjectId, year, section);
    }

    @Override
    public int insertAssignment(int teacherId, int subjectId, int year, String section) {
        return Jdbc.insert("INSERT INTO asignaciones_profesor (id_profesor, id_materia, `año_academico`, seccion) VALUES (?, ?, ?, ?)",
                teacherId, subjectId, year, section);
    }

    @Override
    public boolean hasScheduleConflict(int teacherId, int year, String day, LocalTime start, LocalTime end) {
        return !Jdbc.query("SELECT 1 AS x FROM horarios_clase h JOIN asignaciones_profesor a ON a.id_asignacion = h.id_asignacion "
                + "WHERE a.id_profesor = ? AND a.`año_academico` = ? AND h.dia_semana = ? AND h.hora_inicio < ? AND h.hora_fin > ?",
                rs -> rs.getInt("x"), teacherId, year, day, end, start).isEmpty();
    }

    @Override
    public int insertSchedule(int assignmentId, String day, LocalTime start, LocalTime end) {
        return Jdbc.insert("INSERT INTO horarios_clase (id_asignacion, dia_semana, hora_inicio, hora_fin) VALUES (?, ?, ?, ?)",
                assignmentId, day, start, end);
    }
}
