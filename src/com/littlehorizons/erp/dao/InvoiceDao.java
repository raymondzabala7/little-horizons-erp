package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Invoice;

import java.util.Optional;

public interface InvoiceDao {
    /** Inserts the invoice and returns its consecutive number. */
    int insert(Invoice invoice);
    Optional<Invoice> findByPayment(int paymentId);
    /** Stores the final CUFE and file path once the invoice number is known. */
    void update(Invoice invoice);
}
