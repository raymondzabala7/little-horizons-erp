package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class InvoiceDaoImpl implements InvoiceDao {

    @Override
    public int insertDraft(int paymentId, String provisionalNumber, LocalDate issueDate) {
        return Jdbc.insert("INSERT INTO facturas (numero_factura, id_pago, fecha_emision) VALUES (?, ?, ?)", provisionalNumber, paymentId, issueDate);
    }

    @Override
    public void complete(Invoice i, String fileName) {
        Jdbc.update("UPDATE facturas SET numero_factura = ?, cufe = ?, nombre_archivo = ?, ruta_archivo = ? WHERE id_factura = ?",
                i.number(), i.cufe(), fileName, i.filePath(), i.id());
    }

    @Override
    public Optional<Invoice> findByPayment(int paymentId) {
        return Jdbc.queryOne("SELECT id_factura, numero_factura, id_pago, fecha_emision, cufe, ruta_archivo FROM facturas WHERE id_pago = ?",
                rs -> new Invoice(rs.getInt("id_factura"), rs.getString("numero_factura"), rs.getInt("id_pago"),
                        rs.getObject("fecha_emision", LocalDate.class).atStartOfDay(), BigDecimal.ZERO, BigDecimal.ZERO,
                        BigDecimal.ZERO, rs.getString("cufe"), rs.getString("ruta_archivo")), paymentId);
    }
}
