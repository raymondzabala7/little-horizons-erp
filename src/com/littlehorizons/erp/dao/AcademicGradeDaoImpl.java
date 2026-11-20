package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.AcademicGrade;

import java.util.List;

public class AcademicGradeDaoImpl implements AcademicGradeDao {

    private static final String SQL = "SELECT r.id_registro, r.valor_nota, r.periodo_academico, m.id_materia, m.codigo_materia, m.nombre_materia "
            + "FROM registros_academicos r JOIN materias m ON m.id_materia = r.id_materia";

    @Override
    public List<AcademicGrade> findAll() {
        return Jdbc.query(SQL, rs -> new AcademicGrade(rs.getInt("id_registro"), rs.getBigDecimal("valor_nota"),
                rs.getString("periodo_academico"), rs.getInt("id_materia"), rs.getString("codigo_materia"),
                rs.getString("nombre_materia")));
    }
}
