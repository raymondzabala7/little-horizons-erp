package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.EventParticipation;
import com.littlehorizons.erp.model.SchoolEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EventDaoImpl implements EventDao {

    private static final String SELECT = "SELECT id_evento, nombre_evento, fecha_evento, descripcion, tipo_evento, tiene_competencia, lugar FROM eventos";
    private static final Jdbc.RowMapper<SchoolEvent> MAPPER = rs -> new SchoolEvent(rs.getInt("id_evento"),
            rs.getString("nombre_evento"), rs.getObject("fecha_evento", LocalDate.class), rs.getString("descripcion"),
            rs.getString("tipo_evento"), rs.getBoolean("tiene_competencia"), rs.getString("lugar"));
    private static final String PARTICIPATION_SELECT = "SELECT pe.id_participacion, pe.id_evento, ev.nombre_evento, pe.id_estudiante, "
            + "CONCAT_WS(' ', e.primer_nombre, e.segundo_nombre, e.primer_apellido, e.segundo_apellido) AS nombre, "
            + "COALESCE(pe.posicion_obtenida, pe.rol_participacion) AS posicion FROM participantes_evento pe "
            + "JOIN eventos ev ON ev.id_evento = pe.id_evento JOIN estudiantes e ON e.id_estudiante = pe.id_estudiante";
    private static final Jdbc.RowMapper<EventParticipation> PARTICIPATION_MAPPER = rs -> new EventParticipation(
            rs.getInt("id_participacion"), rs.getInt("id_evento"), rs.getString("nombre_evento"), rs.getInt("id_estudiante"),
            rs.getString("nombre"), rs.getString("posicion"));

    @Override
    public List<SchoolEvent> findAll() {
        return Jdbc.query(SELECT + " ORDER BY fecha_evento", MAPPER);
    }

    @Override
    public Optional<SchoolEvent> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE id_evento = ?", MAPPER, id);
    }

    @Override
    public int insert(String name, LocalDate date, String description, String type, boolean competitive, String place) {
        return Jdbc.insert("INSERT INTO eventos (nombre_evento, fecha_evento, descripcion, tipo_evento, tiene_competencia, lugar, esta_activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, 1)", name, date, description, type, competitive, place);
    }

    @Override
    public void saveParticipation(int eventId, int studentId, String role, String placement) {
        var existing = Jdbc.queryOne("SELECT id_participacion FROM participantes_evento WHERE id_evento = ? AND id_estudiante = ?",
                rs -> rs.getInt(1), eventId, studentId);
        if (existing.isPresent()) {
            Jdbc.update("UPDATE participantes_evento SET rol_participacion = ?, posicion_obtenida = ? WHERE id_participacion = ?",
                    role, placement, existing.get());
        } else {
            Jdbc.insert("INSERT INTO participantes_evento (id_evento, id_estudiante, rol_participacion, posicion_obtenida) VALUES (?, ?, ?, ?)",
                    eventId, studentId, role, placement);
        }
    }

    @Override
    public List<EventParticipation> findParticipants(int eventId) {
        return Jdbc.query(PARTICIPATION_SELECT + " WHERE pe.id_evento = ? ORDER BY pe.posicion_obtenida, e.primer_apellido", PARTICIPATION_MAPPER, eventId);
    }

    @Override
    public List<EventParticipation> findAllParticipations() {
        return Jdbc.query(PARTICIPATION_SELECT, PARTICIPATION_MAPPER);
    }
}
