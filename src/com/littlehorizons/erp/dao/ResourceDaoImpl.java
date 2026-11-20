package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Resource;

import java.util.List;
import java.util.Optional;

public class ResourceDaoImpl implements ResourceDao {

    private static final String SELECT = "SELECT r.id_recurso, r.codigo_recurso, r.nombre_recurso, c.nombre_categoria, r.estado_recurso, "
            + "r.ubicacion_fisica FROM recursos r JOIN categorias_recurso c ON c.id_categoria = r.id_categoria";
    private static final Jdbc.RowMapper<Resource> MAPPER = rs -> new Resource(rs.getInt("id_recurso"), rs.getString("codigo_recurso"),
            rs.getString("nombre_recurso"), rs.getString("nombre_categoria"), rs.getString("estado_recurso"), rs.getString("ubicacion_fisica"));

    @Override
    public List<Resource> findAll() {
        return Jdbc.query(SELECT + " ORDER BY r.codigo_recurso", MAPPER);
    }

    @Override
    public Optional<Resource> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE r.id_recurso = ?", MAPPER, id);
    }

    @Override
    public Optional<Resource> findByCode(String code) {
        return Jdbc.queryOne(SELECT + " WHERE r.codigo_recurso = ?", MAPPER, code);
    }

    @Override
    public List<String> findCategories() {
        return Jdbc.query("SELECT nombre_categoria FROM categorias_recurso ORDER BY id_categoria", rs -> rs.getString(1));
    }

    @Override
    public int insert(String code, String name, String categoryName, String location) {
        return Jdbc.insert("INSERT INTO recursos (codigo_recurso, nombre_recurso, id_categoria, ubicacion_fisica, estado_recurso) "
                + "VALUES (?, ?, (SELECT id_categoria FROM categorias_recurso WHERE nombre_categoria = ?), ?, 'Disponible')",
                code, name, categoryName, location);
    }

    @Override
    public int updateStatus(int resourceId, String status) {
        return Jdbc.update("UPDATE recursos SET estado_recurso = ? WHERE id_recurso = ?", status, resourceId);
    }
}
