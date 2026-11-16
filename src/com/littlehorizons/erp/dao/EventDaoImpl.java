package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.EventParticipation;
import com.littlehorizons.erp.model.SchoolEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EventDaoImpl implements EventDao {

    private static final String SELECT = "SELECT id_evento, nombre, fecha, descripcion, tipo, tiene_competencia FROM eventos";
    private static final Jdbc.RowMapper<SchoolEvent> MAPPER = rs -> new SchoolEvent(rs.getInt("id_evento"),
            rs.getString("nombre"), rs.getObject("fecha", LocalDate.class), rs.getString("descripcion"),
            rs.getString("tipo"), rs.getBoolean("tiene_competencia"));
    private static final String PARTICIPATION_SELECT = "SELECT pe.id_participacion, pe.id_evento, ev.nombre AS evento, pe.id_estudiante, "
            + "e.nombre_completo, pe.posicion FROM participaciones_evento pe JOIN eventos ev ON ev.id_evento = pe.id_evento "
            + "JOIN estudiantes e ON e.id_estudiante = pe.id_estudiante";
    private static final Jdbc.RowMapper<EventParticipation> PARTICIPATION_MAPPER = rs -> new EventParticipation(
            rs.getInt("id_participacion"), rs.getInt("id_evento"), rs.getString("evento"), rs.getInt("id_estudiante"),
            rs.getString("nombre_completo"), rs.getString("posicion"));

    @Override
    public List<SchoolEvent> findAll() {
        return Jdbc.query(SELECT + " ORDER BY fecha", MAPPER);
    }

    @Override
    public Optional<SchoolEvent> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE id_evento = ?", MAPPER, id);
    }

    @Override
    public int insert(String name, LocalDate date, String description, String type, boolean competitive) {
        return Jdbc.insert("INSERT INTO eventos (nombre, fecha, descripcion, tipo, tiene_competencia) VALUES (?, ?, ?, ?, ?)",
                name, date, description, type, competitive);
    }

    @Override
    public void saveParticipation(int eventId, int studentId, String placement) {
        Jdbc.update("INSERT INTO participaciones_evento (id_evento, id_estudiante, posicion) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE posicion = VALUES(posicion)", eventId, studentId, placement);
    }

    @Override
    public List<EventParticipation> findParticipants(int eventId) {
        return Jdbc.query(PARTICIPATION_SELECT + " WHERE pe.id_evento = ? ORDER BY pe.posicion, e.nombre_completo", PARTICIPATION_MAPPER, eventId);
    }

    @Override
    public List<EventParticipation> findAllParticipations() {
        return Jdbc.query(PARTICIPATION_SELECT, PARTICIPATION_MAPPER);
    }
}
