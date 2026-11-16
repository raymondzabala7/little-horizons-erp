package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Guardian;

import java.util.List;
import java.util.Optional;

public class GuardianDaoImpl implements GuardianDao {

    private static final String SELECT = "SELECT id_acudiente, nombre_completo, documento_identidad, telefono, correo, direccion FROM acudientes";
    private static final Jdbc.RowMapper<Guardian> MAPPER = rs -> new Guardian(rs.getInt("id_acudiente"),
            rs.getString("nombre_completo"), rs.getString("documento_identidad"), rs.getString("telefono"),
            rs.getString("correo"), rs.getString("direccion"));

    @Override
    public List<Guardian> findAll() {
        return Jdbc.query(SELECT + " ORDER BY nombre_completo", MAPPER);
    }

    @Override
    public Optional<Guardian> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE id_acudiente = ?", MAPPER, id);
    }

    @Override
    public Optional<Guardian> findByDocument(String documentId) {
        return Jdbc.queryOne(SELECT + " WHERE documento_identidad = ?", MAPPER, documentId);
    }

    @Override
    public int insert(Guardian g) {
        return Jdbc.insert("INSERT INTO acudientes (nombre_completo, documento_identidad, telefono, correo, direccion) VALUES (?, ?, ?, ?, ?)",
                g.fullName(), g.documentId(), g.phone(), g.email(), g.address());
    }
}
