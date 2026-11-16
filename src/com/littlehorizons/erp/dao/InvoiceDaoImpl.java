package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Invoice;

import java.time.LocalDateTime;
import java.util.Optional;

public class InvoiceDaoImpl implements InvoiceDao {

    @Override
    public int insert(Invoice i) {
        return Jdbc.insert("INSERT INTO facturas (id_pago, fecha_emision, subtotal, iva, total, cufe, ruta_archivo) VALUES (?, ?, ?, ?, ?, ?, ?)",
                i.paymentId(), i.issuedAt(), i.subtotal(), i.tax(), i.total(), i.cufe(), i.filePath());
    }

    @Override
    public Optional<Invoice> findByPayment(int paymentId) {
        return Jdbc.queryOne("SELECT id_factura, id_pago, fecha_emision, subtotal, iva, total, cufe, ruta_archivo FROM facturas WHERE id_pago = ?",
                rs -> new Invoice(rs.getInt("id_factura"), rs.getInt("id_pago"), rs.getObject("fecha_emision", LocalDateTime.class),
                        rs.getBigDecimal("subtotal"), rs.getBigDecimal("iva"), rs.getBigDecimal("total"),
                        rs.getString("cufe"), rs.getString("ruta_archivo")), paymentId);
    }

    @Override
    public void update(Invoice i) {
        Jdbc.update("UPDATE facturas SET cufe = ?, ruta_archivo = ? WHERE id_factura = ?", i.cufe(), i.filePath(), i.number());
    }
}
