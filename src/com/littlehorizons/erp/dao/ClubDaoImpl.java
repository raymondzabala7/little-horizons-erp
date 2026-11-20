package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClubDaoImpl implements ClubDao {

    private static final String STUDENT_NAME = "CONCAT_WS(' ', e.primer_nombre, e.segundo_nombre, e.primer_apellido, e.segundo_apellido)";
    private static final String SELECT = "SELECT c.id_club, c.nombre_club, c.descripcion, c.capacidad_maxima, c.id_profesor_supervisor, "
            + "CONCAT_WS(' ', p.primer_nombre, p.primer_apellido) AS profesor, c.esta_activo, "
            + "(SELECT COUNT(*) FROM membresias_club mc WHERE mc.id_club = c.id_club AND mc.estado_membresia = 'Activa') AS miembros "
            + "FROM clubes c LEFT JOIN profesores p ON p.id_profesor = c.id_profesor_supervisor";
    private static final Jdbc.RowMapper<Club> MAPPER = rs -> new Club(rs.getInt("id_club"), rs.getString("nombre_club"),
            rs.getString("descripcion"), rs.getInt("capacidad_maxima"), rs.getInt("id_profesor_supervisor"),
            rs.getString("profesor"), rs.getBoolean("esta_activo"), rs.getInt("miembros"));
    private static final String MEMBERSHIP_SELECT = "SELECT mc.id_membresia, mc.id_club, c.nombre_club, mc.id_estudiante, "
            + STUDENT_NAME + " AS nombre, mc.fecha_ingreso, mc.estado_membresia FROM membresias_club mc "
            + "JOIN clubes c ON c.id_club = mc.id_club JOIN estudiantes e ON e.id_estudiante = mc.id_estudiante";
    private static final Jdbc.RowMapper<ClubMembership> MEMBERSHIP_MAPPER = rs -> new ClubMembership(rs.getInt("id_membresia"),
            rs.getInt("id_club"), rs.getString("nombre_club"), rs.getInt("id_estudiante"), rs.getString("nombre"),
            rs.getObject("fecha_ingreso", LocalDate.class), rs.getString("estado_membresia"));

    @Override
    public List<Club> findAll() {
        return Jdbc.query(SELECT + " ORDER BY c.nombre_club", MAPPER);
    }

    @Override
    public Optional<Club> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE c.id_club = ?", MAPPER, id);
    }

    @Override
    public int insert(String name, String description, int maxCapacity, int teacherId) {
        return Jdbc.insert("INSERT INTO clubes (nombre_club, descripcion, id_profesor_supervisor, capacidad_maxima, esta_activo) VALUES (?, ?, ?, ?, 1)",
                name, description, teacherId, maxCapacity);
    }

    @Override
    public boolean isActiveMember(int clubId, int studentId) {
        return !Jdbc.query("SELECT 1 AS x FROM membresias_club WHERE id_club = ? AND id_estudiante = ? AND estado_membresia = 'Activa'",
                rs -> rs.getInt("x"), clubId, studentId).isEmpty();
    }

    @Override
    public void addMember(int clubId, int studentId, LocalDate joinDate) {
        var previous = Jdbc.queryOne("SELECT id_membresia FROM membresias_club WHERE id_club = ? AND id_estudiante = ?",
                rs -> rs.getInt(1), clubId, studentId);
        if (previous.isPresent()) {
            Jdbc.update("UPDATE membresias_club SET estado_membresia = 'Activa', fecha_ingreso = ?, fecha_retiro = NULL, motivo_retiro = NULL "
                    + "WHERE id_membresia = ?", joinDate, previous.get());
        } else {
            Jdbc.insert("INSERT INTO membresias_club (id_estudiante, id_club, fecha_ingreso, estado_membresia) VALUES (?, ?, ?, 'Activa')",
                    studentId, clubId, joinDate);
        }
    }

    @Override
    public int withdrawMember(int clubId, int studentId, LocalDate date) {
        return Jdbc.update("UPDATE membresias_club SET estado_membresia = 'Retirada', fecha_retiro = ? "
                + "WHERE id_club = ? AND id_estudiante = ? AND estado_membresia = 'Activa'", date, clubId, studentId);
    }

    @Override
    public List<ClubMembership> findMembers(int clubId) {
        return Jdbc.query(MEMBERSHIP_SELECT + " WHERE mc.id_club = ? ORDER BY e.primer_apellido", MEMBERSHIP_MAPPER, clubId);
    }

    @Override
    public List<ClubMembership> findMembershipsByStudent(int studentId) {
        return Jdbc.query(MEMBERSHIP_SELECT + " WHERE mc.id_estudiante = ? ORDER BY c.nombre_club", MEMBERSHIP_MAPPER, studentId);
    }
}
