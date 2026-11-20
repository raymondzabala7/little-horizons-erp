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
    int insertConcept(String code, String name, BigDecimal baseValue, boolean recurring);
    Optional<BigDecimal> findCustomValue(int studentId, int conceptId, int year);
    void saveCustomValue(int studentId, int conceptId, int year, BigDecimal value, String reason);
    /** Inserts a payment; the payer is the student's principal guardian. */
    int insertPayment(int studentId, int conceptId, BigDecimal amount, LocalDate date, String method, String receipt,
                      Integer year, String period, Integer month);
    boolean isMonthPaid(int studentId, int conceptId, int year, int month);
    /** Pairs {studentId, month} already paid for the concept in the year. */
    List<int[]> findPaidMonths(int conceptId, int year);
    Optional<Payment> findById(int id);
    List<Payment> findByStudent(int studentId);
    List<Payment> findByGuardian(int guardianId);
    List<Payment> findAll();
    List<Payment> findWithoutInvoice();
    String nextReceiptNumber();
}
