package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClubDaoImpl implements ClubDao {

    private static final String SELECT = "SELECT c.id_club, c.nombre, c.descripcion, c.cupo_maximo, c.id_profesor, p.nombre_completo AS profesor, "
            + "c.activo, (SELECT COUNT(*) FROM inscripciones_club ic WHERE ic.id_club = c.id_club AND ic.estado = 'Activo') AS miembros "
            + "FROM clubes c JOIN profesores p ON p.id_profesor = c.id_profesor";
    private static final Jdbc.RowMapper<Club> MAPPER = rs -> new Club(rs.getInt("id_club"), rs.getString("nombre"),
            rs.getString("descripcion"), rs.getInt("cupo_maximo"), rs.getInt("id_profesor"), rs.getString("profesor"),
            rs.getBoolean("activo"), rs.getInt("miembros"));
    private static final String MEMBERSHIP_SELECT = "SELECT ic.id_inscripcion_club, ic.id_club, c.nombre AS club, ic.id_estudiante, "
            + "e.nombre_completo, ic.fecha_ingreso, ic.estado FROM inscripciones_club ic "
            + "JOIN clubes c ON c.id_club = ic.id_club JOIN estudiantes e ON e.id_estudiante = ic.id_estudiante";
    private static final Jdbc.RowMapper<ClubMembership> MEMBERSHIP_MAPPER = rs -> new ClubMembership(
            rs.getInt("id_inscripcion_club"), rs.getInt("id_club"), rs.getString("club"), rs.getInt("id_estudiante"),
            rs.getString("nombre_completo"), rs.getObject("fecha_ingreso", LocalDate.class), rs.getString("estado"));

    @Override
    public List<Club> findAll() {
        return Jdbc.query(SELECT + " ORDER BY c.nombre", MAPPER);
    }

    @Override
    public Optional<Club> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE c.id_club = ?", MAPPER, id);
    }

    @Override
    public int insert(String name, String description, int maxCapacity, int teacherId) {
        return Jdbc.insert("INSERT INTO clubes (nombre, descripcion, cupo_maximo, id_profesor, activo) VALUES (?, ?, ?, ?, TRUE)",
                name, description, maxCapacity, teacherId);
    }

    @Override
    public boolean isActiveMember(int clubId, int studentId) {
        return !Jdbc.query("SELECT 1 AS x FROM inscripciones_club WHERE id_club = ? AND id_estudiante = ? AND estado = 'Activo'",
                rs -> rs.getInt("x"), clubId, studentId).isEmpty();
    }

    @Override
    public void addMember(int clubId, int studentId, LocalDate joinDate) {
        Jdbc.update("INSERT INTO inscripciones_club (id_club, id_estudiante, fecha_ingreso, estado) VALUES (?, ?, ?, 'Activo') "
                + "ON DUPLICATE KEY UPDATE estado = 'Activo', fecha_ingreso = VALUES(fecha_ingreso)", clubId, studentId, joinDate);
    }

    @Override
    public int withdrawMember(int clubId, int studentId) {
        return Jdbc.update("UPDATE inscripciones_club SET estado = 'Retirado' WHERE id_club = ? AND id_estudiante = ? AND estado = 'Activo'",
                clubId, studentId);
    }

    @Override
    public List<ClubMembership> findMembers(int clubId) {
        return Jdbc.query(MEMBERSHIP_SELECT + " WHERE ic.id_club = ? ORDER BY e.nombre_completo", MEMBERSHIP_MAPPER, clubId);
    }

    @Override
    public List<ClubMembership> findMembershipsByStudent(int studentId) {
        return Jdbc.query(MEMBERSHIP_SELECT + " WHERE ic.id_estudiante = ? ORDER BY c.nombre", MEMBERSHIP_MAPPER, studentId);
    }
}
