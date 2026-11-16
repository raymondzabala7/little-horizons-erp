package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentDao {
    List<PaymentConcept> findConcepts();
    Optional<PaymentConcept> findConceptById(int id);
    int insertConcept(String name, BigDecimal baseValue, boolean taxable);
    Optional<BigDecimal> findCustomValue(int studentId, int conceptId);
    void saveCustomValue(int studentId, int conceptId, BigDecimal value, String reason);
    int insertCharge(int studentId, int conceptId, BigDecimal amount, LocalDate dueDate);
    Optional<Payment> findById(int id);
    List<Payment> findByStudent(int studentId);
    List<Payment> findByGuardian(int guardianId);
    List<Payment> findAll();
    List<Payment> findPending();
    List<Payment> findPaidWithoutInvoice();
    String nextReceiptNumber();
    int markPaid(int paymentId, LocalDate paymentDate, String method, String receiptNumber);
}
