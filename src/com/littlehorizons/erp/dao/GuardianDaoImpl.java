package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.GuardianForm;

import java.util.List;
import java.util.Optional;

public class GuardianDaoImpl implements GuardianDao {

    private static final String SELECT = "SELECT a.id_acudiente, "
            + "CONCAT_WS(' ', a.primer_nombre, a.segundo_nombre, a.primer_apellido, a.segundo_apellido) AS nombre, "
            + "a.tipo_documento, a.numero_documento, a.parentesco, a.telefono_principal, a.correo_electronico, a.direccion FROM acudientes a";
    private static final Jdbc.RowMapper<Guardian> MAPPER = rs -> new Guardian(rs.getInt("id_acudiente"), rs.getString("nombre"),
            rs.getString("tipo_documento"), rs.getString("numero_documento"), rs.getString("parentesco"),
            rs.getString("telefono_principal"), rs.getString("correo_electronico"), rs.getString("direccion"));

    @Override
    public List<Guardian> findAll() {
        return Jdbc.query(SELECT + " ORDER BY a.primer_apellido, a.primer_nombre", MAPPER);
    }

    @Override
    public Optional<Guardian> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE a.id_acudiente = ?", MAPPER, id);
    }

    @Override
    public Optional<Guardian> findByDocument(String documentId) {
        return Jdbc.queryOne(SELECT + " WHERE a.numero_documento = ?", MAPPER, documentId);
    }

    @Override
    public Optional<Guardian> findPrincipalOfStudent(int studentId) {
        return Jdbc.queryOne(SELECT + " JOIN estudiante_acudiente ea ON ea.id_acudiente = a.id_acudiente "
                + "WHERE ea.id_estudiante = ? ORDER BY ea.es_principal DESC, a.id_acudiente LIMIT 1", MAPPER, studentId);
    }

    @Override
    public int insert(GuardianForm f) {
        return Jdbc.insert("INSERT INTO acudientes (tipo_documento, numero_documento, primer_nombre, segundo_nombre, primer_apellido, "
                        + "segundo_apellido, parentesco, telefono_principal, correo_electronico, direccion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                f.documentType(), f.documentId(), f.name().first(), f.name().middle(), f.name().last(), f.name().secondLast(),
                f.relationship(), f.phone(), f.email(), f.address());
    }
}
