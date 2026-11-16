package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Resource;

import java.util.List;
import java.util.Optional;

public class ResourceDaoImpl implements ResourceDao {

    private static final String SELECT = "SELECT id_recurso, codigo, nombre, categoria, estado, ubicacion FROM recursos";
    private static final Jdbc.RowMapper<Resource> MAPPER = rs -> new Resource(rs.getInt("id_recurso"), rs.getString("codigo"),
            rs.getString("nombre"), rs.getString("categoria"), rs.getString("estado"), rs.getString("ubicacion"));

    @Override
    public List<Resource> findAll() {
        return Jdbc.query(SELECT + " ORDER BY codigo", MAPPER);
    }

    @Override
    public Optional<Resource> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE id_recurso = ?", MAPPER, id);
    }

    @Override
    public Optional<Resource> findByCode(String code) {
        return Jdbc.queryOne(SELECT + " WHERE codigo = ?", MAPPER, code);
    }

    @Override
    public int insert(String code, String name, String category, String location) {
        return Jdbc.insert("INSERT INTO recursos (codigo, nombre, categoria, estado, ubicacion) VALUES (?, ?, ?, 'Disponible', ?)",
                code, name, category, location);
    }

    @Override
    public int updateStatus(int resourceId, String status) {
        return Jdbc.update("UPDATE recursos SET estado = ? WHERE id_recurso = ?", status, resourceId);
    }
}
