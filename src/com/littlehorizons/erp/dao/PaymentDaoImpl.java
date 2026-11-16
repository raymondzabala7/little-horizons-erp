package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PaymentDaoImpl implements PaymentDao {

    private static final Jdbc.RowMapper<PaymentConcept> CONCEPT_MAPPER = rs -> new PaymentConcept(
            rs.getInt("id_concepto"), rs.getString("nombre"), rs.getBigDecimal("valor_base"), rs.getBoolean("aplica_iva"));
    private static final String SELECT = "SELECT p.id_pago, p.id_estudiante, e.nombre_completo, p.id_concepto, c.nombre AS concepto, "
            + "p.valor, p.fecha_vencimiento, p.fecha_pago, p.metodo_pago, p.numero_recibo, p.estado, c.aplica_iva "
            + "FROM pagos p JOIN estudiantes e ON e.id_estudiante = p.id_estudiante JOIN conceptos_pago c ON c.id_concepto = p.id_concepto";
    private static final Jdbc.RowMapper<Payment> MAPPER = rs -> new Payment(rs.getInt("id_pago"), rs.getInt("id_estudiante"),
            rs.getString("nombre_completo"), rs.getInt("id_concepto"), rs.getString("concepto"), rs.getBigDecimal("valor"),
            rs.getObject("fecha_vencimiento", LocalDate.class), rs.getObject("fecha_pago", LocalDate.class),
            rs.getString("metodo_pago"), rs.getString("numero_recibo"), rs.getString("estado"), rs.getBoolean("aplica_iva"));

    @Override
    public List<PaymentConcept> findConcepts() {
        return Jdbc.query("SELECT id_concepto, nombre, valor_base, aplica_iva FROM conceptos_pago ORDER BY id_concepto", CONCEPT_MAPPER);
    }

    @Override
    public Optional<PaymentConcept> findConceptById(int id) {
        return Jdbc.queryOne("SELECT id_concepto, nombre, valor_base, aplica_iva FROM conceptos_pago WHERE id_concepto = ?", CONCEPT_MAPPER, id);
    }

    @Override
    public int insertConcept(String name, BigDecimal baseValue, boolean taxable) {
        return Jdbc.insert("INSERT INTO conceptos_pago (nombre, valor_base, aplica_iva) VALUES (?, ?, ?)", name, baseValue, taxable);
    }

    @Override
    public Optional<BigDecimal> findCustomValue(int studentId, int conceptId) {
        return Jdbc.queryOne("SELECT valor FROM valores_personalizados WHERE id_estudiante = ? AND id_concepto = ?",
                rs -> rs.getBigDecimal("valor"), studentId, conceptId);
    }

    @Override
    public void saveCustomValue(int studentId, int conceptId, BigDecimal value, String reason) {
        Jdbc.update("INSERT INTO valores_personalizados (id_estudiante, id_concepto, valor, motivo) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE valor = VALUES(valor), motivo = VALUES(motivo)", studentId, conceptId, value, reason);
    }

    @Override
    public int insertCharge(int studentId, int conceptId, BigDecimal amount, LocalDate dueDate) {
        return Jdbc.insert("INSERT INTO pagos (id_estudiante, id_concepto, valor, fecha_vencimiento, estado) VALUES (?, ?, ?, ?, 'Pendiente')",
                studentId, conceptId, amount, dueDate);
    }

    @Override
    public Optional<Payment> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE p.id_pago = ?", MAPPER, id);
    }

    @Override
    public List<Payment> findByStudent(int studentId) {
        return Jdbc.query(SELECT + " WHERE p.id_estudiante = ? ORDER BY p.fecha_vencimiento", MAPPER, studentId);
    }

    @Override
    public List<Payment> findByGuardian(int guardianId) {
        return Jdbc.query(SELECT + " WHERE e.id_acudiente = ? ORDER BY e.nombre_completo, p.fecha_vencimiento", MAPPER, guardianId);
    }

    @Override
    public List<Payment> findAll() {
        return Jdbc.query(SELECT + " ORDER BY p.fecha_vencimiento", MAPPER);
    }

    @Override
    public List<Payment> findPending() {
        return Jdbc.query(SELECT + " WHERE p.estado = 'Pendiente' ORDER BY p.fecha_vencimiento", MAPPER);
    }

    @Override
    public List<Payment> findPaidWithoutInvoice() {
        return Jdbc.query(SELECT + " WHERE p.estado = 'Pagado' AND NOT EXISTS (SELECT 1 FROM facturas f WHERE f.id_pago = p.id_pago) "
                + "ORDER BY p.fecha_pago DESC LIMIT 50", MAPPER);
    }

    @Override
    public String nextReceiptNumber() {
        int next = Jdbc.queryOne("SELECT COALESCE(MAX(CAST(SUBSTRING(numero_recibo, 5) AS UNSIGNED)), 0) + 1 AS n FROM pagos",
                rs -> rs.getInt("n")).orElse(1);
        return String.format("REC-%06d", next);
    }

    @Override
    public int markPaid(int paymentId, LocalDate paymentDate, String method, String receiptNumber) {
        return Jdbc.update("UPDATE pagos SET estado = 'Pagado', fecha_pago = ?, metodo_pago = ?, numero_recibo = ? "
                + "WHERE id_pago = ? AND estado = 'Pendiente'", paymentDate, method, receiptNumber, paymentId);
    }
}
