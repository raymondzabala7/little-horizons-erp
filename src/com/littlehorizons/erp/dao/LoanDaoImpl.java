package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Loan;

import java.time.LocalDate;
import java.util.List;

public class LoanDaoImpl implements LoanDao {

    private static final String STATUS = "CASE WHEN l.estado = 'Activo' AND l.fecha_devolucion_programada < CURDATE() THEN 'Vencido' ELSE l.estado END";
    private static final String UNION = "SELECT * FROM ("
            + "SELECT l.id_prestamo, l.id_recurso, r.codigo, r.nombre AS recurso, 'Estudiante' AS tipo, e.id_estudiante AS id_prestatario, "
            + "e.nombre_completo AS prestatario, l.fecha_prestamo, l.fecha_devolucion_programada, l.fecha_devolucion_real, " + STATUS + " AS estado_calc "
            + "FROM prestamos_estudiante l JOIN recursos r ON r.id_recurso = l.id_recurso JOIN estudiantes e ON e.id_estudiante = l.id_estudiante "
            + "UNION ALL "
            + "SELECT l.id_prestamo, l.id_recurso, r.codigo, r.nombre, 'Profesor', p.id_profesor, p.nombre_completo, "
            + "l.fecha_prestamo, l.fecha_devolucion_programada, l.fecha_devolucion_real, " + STATUS + " "
            + "FROM prestamos_profesor l JOIN recursos r ON r.id_recurso = l.id_recurso JOIN profesores p ON p.id_profesor = l.id_profesor"
            + ") loans";
    private static final Jdbc.RowMapper<Loan> MAPPER = rs -> new Loan(rs.getInt("id_prestamo"), rs.getInt("id_recurso"),
            rs.getString("codigo"), rs.getString("recurso"), rs.getString("tipo"), rs.getInt("id_prestatario"),
            rs.getString("prestatario"), rs.getObject("fecha_prestamo", LocalDate.class),
            rs.getObject("fecha_devolucion_programada", LocalDate.class), rs.getObject("fecha_devolucion_real", LocalDate.class),
            rs.getString("estado_calc"));

    @Override
    public List<Loan> findOpen() {
        return Jdbc.query(UNION + " WHERE estado_calc <> 'Devuelto' ORDER BY fecha_devolucion_programada", MAPPER);
    }

    @Override
    public List<Loan> findOverdue() {
        return Jdbc.query(UNION + " WHERE estado_calc = 'Vencido' ORDER BY fecha_devolucion_programada", MAPPER);
    }

    @Override
    public List<Loan> findByBorrower(String borrowerType, int borrowerId) {
        return Jdbc.query(UNION + " WHERE tipo = ? AND id_prestatario = ? ORDER BY fecha_prestamo DESC", MAPPER, borrowerType, borrowerId);
    }

    @Override
    public int insert(int resourceId, String borrowerType, int borrowerId, LocalDate loanDate, LocalDate dueDate) {
        String table = Loan.STUDENT.equals(borrowerType) ? "prestamos_estudiante" : "prestamos_profesor";
        String column = Loan.STUDENT.equals(borrowerType) ? "id_estudiante" : "id_profesor";
        return Jdbc.insert("INSERT INTO " + table + " (id_recurso, " + column + ", fecha_prestamo, fecha_devolucion_programada, estado) "
                + "VALUES (?, ?, ?, ?, 'Activo')", resourceId, borrowerId, loanDate, dueDate);
    }

    @Override
    public int markReturned(int resourceId, LocalDate returnDate) {
        int a = Jdbc.update("UPDATE prestamos_estudiante SET estado = 'Devuelto', fecha_devolucion_real = ? WHERE id_recurso = ? AND estado <> 'Devuelto'",
                returnDate, resourceId);
        int b = Jdbc.update("UPDATE prestamos_profesor SET estado = 'Devuelto', fecha_devolucion_real = ? WHERE id_recurso = ? AND estado <> 'Devuelto'",
                returnDate, resourceId);
        return a + b;
    }

    @Override
    public int markOverdue() {
        return Jdbc.update("UPDATE prestamos_estudiante SET estado = 'Vencido' WHERE estado = 'Activo' AND fecha_devolucion_programada < CURDATE()")
                + Jdbc.update("UPDATE prestamos_profesor SET estado = 'Vencido' WHERE estado = 'Activo' AND fecha_devolucion_programada < CURDATE()");
    }
}
