package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Loan;

import java.time.LocalDate;
import java.util.List;

public class LoanDaoImpl implements LoanDao {

    private static final String STATUS = "CASE WHEN l.estado_prestamo = 'Activo' AND l.fecha_devolucion_esperada < CURDATE() THEN 'Vencido' ELSE l.estado_prestamo END";
    private static final String UNION = "SELECT * FROM ("
            + "SELECT l.id_prestamo, l.id_recurso, r.codigo_recurso, r.nombre_recurso, 'Estudiante' AS tipo, e.id_estudiante AS id_prestatario, "
            + "CONCAT_WS(' ', e.primer_nombre, e.segundo_nombre, e.primer_apellido, e.segundo_apellido) AS prestatario, l.fecha_prestamo, "
            + "l.fecha_devolucion_esperada, l.fecha_devolucion_real, " + STATUS + " AS estado_calc "
            + "FROM prestamos_estudiante l JOIN recursos r ON r.id_recurso = l.id_recurso JOIN estudiantes e ON e.id_estudiante = l.id_estudiante "
            + "UNION ALL "
            + "SELECT l.id_prestamo, l.id_recurso, r.codigo_recurso, r.nombre_recurso, 'Profesor', p.id_profesor, "
            + "CONCAT_WS(' ', p.primer_nombre, p.segundo_nombre, p.primer_apellido, p.segundo_apellido), l.fecha_prestamo, "
            + "l.fecha_devolucion_esperada, l.fecha_devolucion_real, " + STATUS + " "
            + "FROM prestamos_profesor l JOIN recursos r ON r.id_recurso = l.id_recurso JOIN profesores p ON p.id_profesor = l.id_profesor"
            + ") loans";
    private static final Jdbc.RowMapper<Loan> MAPPER = rs -> new Loan(rs.getInt("id_prestamo"), rs.getInt("id_recurso"),
            rs.getString("codigo_recurso"), rs.getString("nombre_recurso"), rs.getString("tipo"), rs.getInt("id_prestatario"),
            rs.getString("prestatario"), rs.getObject("fecha_prestamo", LocalDate.class),
            rs.getObject("fecha_devolucion_esperada", LocalDate.class), rs.getObject("fecha_devolucion_real", LocalDate.class),
            rs.getString("estado_calc"));

    @Override
    public List<Loan> findOpen() {
        return Jdbc.query(UNION + " WHERE estado_calc <> 'Devuelto' ORDER BY fecha_devolucion_esperada", MAPPER);
    }

    @Override
    public List<Loan> findOverdue() {
        return Jdbc.query(UNION + " WHERE estado_calc = 'Vencido' ORDER BY fecha_devolucion_esperada", MAPPER);
    }

    @Override
    public List<Loan> findByBorrower(String borrowerType, int borrowerId) {
        return Jdbc.query(UNION + " WHERE tipo = ? AND id_prestatario = ? ORDER BY fecha_prestamo DESC", MAPPER, borrowerType, borrowerId);
    }

    @Override
    public int insert(int resourceId, String borrowerType, int borrowerId, LocalDate loanDate, LocalDate dueDate) {
        boolean student = Loan.STUDENT.equals(borrowerType);
        return Jdbc.insert("INSERT INTO " + (student ? "prestamos_estudiante" : "prestamos_profesor") + " (id_recurso, "
                + (student ? "id_estudiante" : "id_profesor") + ", fecha_prestamo, fecha_devolucion_esperada, estado_prestamo) VALUES (?, ?, ?, ?, 'Activo')",
                resourceId, borrowerId, loanDate, dueDate);
    }

    @Override
    public int markReturned(int resourceId, LocalDate returnDate) {
        return Jdbc.update("UPDATE prestamos_estudiante SET estado_prestamo = 'Devuelto', fecha_devolucion_real = ? WHERE id_recurso = ? AND estado_prestamo <> 'Devuelto'",
                returnDate, resourceId)
                + Jdbc.update("UPDATE prestamos_profesor SET estado_prestamo = 'Devuelto', fecha_devolucion_real = ? WHERE id_recurso = ? AND estado_prestamo <> 'Devuelto'",
                returnDate, resourceId);
    }

    @Override
    public int markOverdue() {
        return Jdbc.update("UPDATE prestamos_estudiante SET estado_prestamo = 'Vencido' WHERE estado_prestamo = 'Activo' AND fecha_devolucion_esperada < CURDATE()")
                + Jdbc.update("UPDATE prestamos_profesor SET estado_prestamo = 'Vencido' WHERE estado_prestamo = 'Activo' AND fecha_devolucion_esperada < CURDATE()");
    }
}
