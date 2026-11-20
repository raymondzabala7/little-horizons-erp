package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Invoice;

import java.time.LocalDate;
import java.util.Optional;

public interface InvoiceDao {
    /** Reserves the consecutive number by inserting a provisional row; returns its id. */
    int insertDraft(int paymentId, String provisionalNumber, LocalDate issueDate);
    /** Stores the final invoice number, CUFE and file location. */
    void complete(Invoice invoice, String fileName);
    Optional<Invoice> findByPayment(int paymentId);
}
