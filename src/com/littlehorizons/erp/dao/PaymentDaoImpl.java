package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PaymentDaoImpl implements PaymentDao {

    private static final String CONCEPT_SELECT = "SELECT id_concepto, codigo_concepto, nombre_concepto, monto_base, es_recurrente FROM conceptos_pago";
    private static final Jdbc.RowMapper<PaymentConcept> CONCEPT_MAPPER = rs -> new PaymentConcept(rs.getInt("id_concepto"),
            rs.getString("codigo_concepto"), rs.getString("nombre_concepto"), rs.getBigDecimal("monto_base"), rs.getBoolean("es_recurrente"));
    private static final String SELECT = "SELECT p.id_pago, p.id_estudiante, "
            + "CONCAT_WS(' ', e.primer_nombre, e.segundo_nombre, e.primer_apellido, e.segundo_apellido) AS nombre, "
            + "p.id_concepto, c.codigo_concepto, c.nombre_concepto, p.monto_pagado, p.fecha_pago, p.metodo_pago, p.numero_recibo, "
            + "p.`año_academico` AS anio, p.periodo_academico, p.mes_aplicado FROM pagos p "
            + "JOIN estudiantes e ON e.id_estudiante = p.id_estudiante JOIN conceptos_pago c ON c.id_concepto = p.id_concepto";
    private static final Jdbc.RowMapper<Payment> MAPPER = rs -> new Payment(rs.getInt("id_pago"), rs.getInt("id_estudiante"),
            rs.getString("nombre"), rs.getInt("id_concepto"), rs.getString("codigo_concepto"), rs.getString("nombre_concepto"),
            rs.getBigDecimal("monto_pagado"), rs.getObject("fecha_pago", LocalDate.class), rs.getString("metodo_pago"),
            rs.getString("numero_recibo"), (Integer) rs.getObject("anio"), rs.getString("periodo_academico"),
            (Integer) rs.getObject("mes_aplicado"));

    @Override
    public List<PaymentConcept> findConcepts() {
        return Jdbc.query(CONCEPT_SELECT + " WHERE esta_activo = 1 ORDER BY id_concepto", CONCEPT_MAPPER);
    }

    @Override
    public Optional<PaymentConcept> findConceptById(int id) {
        return Jdbc.queryOne(CONCEPT_SELECT + " WHERE id_concepto = ?", CONCEPT_MAPPER, id);
    }

    @Override
    public int insertConcept(String code, String name, BigDecimal baseValue, boolean recurring) {
        return Jdbc.insert("INSERT INTO conceptos_pago (codigo_concepto, nombre_concepto, monto_base, es_recurrente, esta_activo) VALUES (?, ?, ?, ?, 1)",
                code, name, baseValue, recurring);
    }

    @Override
    public Optional<BigDecimal> findCustomValue(int studentId, int conceptId, int year) {
        return Jdbc.queryOne("SELECT valor_personalizado FROM valores_personalizados WHERE id_estudiante = ? AND id_concepto = ? AND `año_academico` = ?",
                rs -> rs.getBigDecimal(1), studentId, conceptId, year);
    }

    @Override
    public void saveCustomValue(int studentId, int conceptId, int year, BigDecimal value, String reason) {
        int updated = Jdbc.update("UPDATE valores_personalizados SET valor_personalizado = ?, motivo = ? "
                + "WHERE id_estudiante = ? AND id_concepto = ? AND `año_academico` = ?", value, reason, studentId, conceptId, year);
        if (updated == 0) {
            Jdbc.insert("INSERT INTO valores_personalizados (id_estudiante, id_concepto, `año_academico`, valor_personalizado, motivo) VALUES (?, ?, ?, ?, ?)",
                    studentId, conceptId, year, value, reason);
        }
    }

    @Override
    public int insertPayment(int studentId, int conceptId, BigDecimal amount, LocalDate date, String method, String receipt,
                             Integer year, String period, Integer month) {
        return Jdbc.insert("INSERT INTO pagos (id_estudiante, id_acudiente, id_concepto, monto_pagado, fecha_pago, metodo_pago, numero_recibo, "
                        + "`año_academico`, periodo_academico, mes_aplicado) VALUES (?, (SELECT id_acudiente FROM estudiante_acudiente WHERE id_estudiante = ? "
                        + "ORDER BY es_principal DESC LIMIT 1), ?, ?, ?, ?, ?, ?, ?, ?)",
                studentId, studentId, conceptId, amount, date, method, receipt, year, period, month);
    }

    @Override
    public boolean isMonthPaid(int studentId, int conceptId, int year, int month) {
        return !Jdbc.query("SELECT 1 AS x FROM pagos WHERE id_estudiante = ? AND id_concepto = ? AND `año_academico` = ? AND mes_aplicado = ?",
                rs -> rs.getInt("x"), studentId, conceptId, year, month).isEmpty();
    }

    @Override
    public List<int[]> findPaidMonths(int conceptId, int year) {
        return Jdbc.query("SELECT id_estudiante, mes_aplicado FROM pagos WHERE id_concepto = ? AND `año_academico` = ? AND mes_aplicado IS NOT NULL",
                rs -> new int[]{rs.getInt(1), rs.getInt(2)}, conceptId, year);
    }

    @Override
    public Optional<Payment> findById(int id) {
        return Jdbc.queryOne(SELECT + " WHERE p.id_pago = ?", MAPPER, id);
    }

    @Override
    public List<Payment> findByStudent(int studentId) {
        return Jdbc.query(SELECT + " WHERE p.id_estudiante = ? ORDER BY p.fecha_pago DESC", MAPPER, studentId);
    }

    @Override
    public List<Payment> findByGuardian(int guardianId) {
        return Jdbc.query(SELECT + " WHERE p.id_estudiante IN (SELECT id_estudiante FROM estudiante_acudiente WHERE id_acudiente = ?) "
                + "ORDER BY e.primer_nombre, p.fecha_pago DESC", MAPPER, guardianId);
    }

    @Override
    public List<Payment> findAll() {
        return Jdbc.query(SELECT + " ORDER BY p.fecha_pago", MAPPER);
    }

    @Override
    public List<Payment> findWithoutInvoice() {
        return Jdbc.query(SELECT + " WHERE NOT EXISTS (SELECT 1 FROM facturas f WHERE f.id_pago = p.id_pago) ORDER BY p.fecha_pago DESC LIMIT 50", MAPPER);
    }

    @Override
    public String nextReceiptNumber() {
        int next = Jdbc.queryOne("SELECT COALESCE(MAX(id_pago), 0) + 1 FROM pagos", rs -> rs.getInt(1)).orElse(1);
        while (!Jdbc.query("SELECT 1 FROM pagos WHERE numero_recibo = ?", rs -> rs.getInt(1), String.format("REC-%06d", next)).isEmpty()) {
            next++;
        }
        return String.format("REC-%06d", next);
    }
}
