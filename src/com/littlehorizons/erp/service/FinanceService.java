package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.GuardianDao;
import com.littlehorizons.erp.dao.InvoiceDao;
import com.littlehorizons.erp.dao.Jdbc;
import com.littlehorizons.erp.dao.PaymentDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.exception.BusinessRuleException;
import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;
import com.littlehorizons.erp.model.Student;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Payment concepts, charges, payments, delinquency and electronic (text) invoices. */
public class FinanceService {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.19");

    private final PaymentDao paymentDao;
    private final InvoiceDao invoiceDao;
    private final StudentDao studentDao;
    private final GuardianDao guardianDao;
    private final InvoiceTextWriter invoiceWriter;

    public FinanceService(PaymentDao paymentDao, InvoiceDao invoiceDao, StudentDao studentDao,
                          GuardianDao guardianDao, InvoiceTextWriter invoiceWriter) {
        this.paymentDao = paymentDao;
        this.invoiceDao = invoiceDao;
        this.studentDao = studentDao;
        this.guardianDao = guardianDao;
        this.invoiceWriter = invoiceWriter;
    }

    // ---------- concepts ----------

    public List<PaymentConcept> listConcepts() {
        return paymentDao.findConcepts();
    }

    public int createConcept(String name, BigDecimal baseValue, boolean taxable) {
        return paymentDao.insertConcept(name, baseValue, taxable);
    }

    public PaymentConcept getConcept(int id) {
        return paymentDao.findConceptById(id).orElseThrow(() -> new BusinessRuleException("Concepto " + id + " no encontrado."));
    }

    public void setCustomValue(int studentId, int conceptId, BigDecimal value, String reason) {
        requireStudent(studentId);
        getConcept(conceptId);
        paymentDao.saveCustomValue(studentId, conceptId, value, reason);
    }

    // ---------- charges and payments ----------

    private Student requireStudent(int studentId) {
        return studentDao.findById(studentId).orElseThrow(() -> new BusinessRuleException("Estudiante " + studentId + " no encontrado."));
    }

    /** Creates a pending charge using the student's custom value when there is one. */
    public int createCharge(int studentId, int conceptId, LocalDate dueDate) {
        requireStudent(studentId);
        PaymentConcept concept = getConcept(conceptId);
        BigDecimal amount = paymentDao.findCustomValue(studentId, conceptId).orElse(concept.baseValue());
        if (amount.signum() <= 0) {
            throw new BusinessRuleException("El valor a cobrar debe ser mayor que cero.");
        }
        return paymentDao.insertCharge(studentId, conceptId, amount, dueDate);
    }

    public Payment getPayment(int paymentId) {
        return paymentDao.findById(paymentId).orElseThrow(() -> new BusinessRuleException("Pago " + paymentId + " no encontrado."));
    }

    public List<Payment> pendingPayments() {
        return paymentDao.findPending();
    }

    public List<Payment> paidWithoutInvoice() {
        return paymentDao.findPaidWithoutInvoice();
    }

    /**
     * Confirms a pending payment and issues its invoice in one transaction:
     * if the invoice file cannot be written, the payment stays pending.
     */
    public Invoice confirmPayment(int paymentId, String method) {
        if (!Catalog.PAYMENT_METHODS.contains(method)) {
            throw new BusinessRuleException("Metodo de pago invalido: " + method);
        }
        return Jdbc.transaction(() -> {
            Payment pending = getPayment(paymentId);
            if (pending.isPaid()) {
                throw new BusinessRuleException("Ese pago ya fue confirmado.");
            }
            String receipt = paymentDao.nextReceiptNumber();
            if (paymentDao.markPaid(paymentId, LocalDate.now(), method, receipt) == 0) {
                throw new BusinessRuleException("No se pudo confirmar el pago.");
            }
            return issueInvoice(getPayment(paymentId));
        });
    }

    /** Issues the invoice of an already paid payment that has none (or returns the existing one). */
    public Invoice generateInvoice(int paymentId) {
        Payment payment = getPayment(paymentId);
        if (!payment.isPaid()) {
            throw new BusinessRuleException("Solo se pueden facturar pagos ya realizados.");
        }
        return invoiceDao.findByPayment(paymentId).orElseGet(() -> Jdbc.transaction(() -> issueInvoice(payment)));
    }

    private Invoice issueInvoice(Payment payment) {
        Student student = requireStudent(payment.studentId());
        Guardian customer = guardianDao.findById(student.guardianId())
                .orElseThrow(() -> new BusinessRuleException("El estudiante no tiene acudiente a quien facturar."));
        BigDecimal total = payment.amount();
        BigDecimal subtotal = payment.taxable()
                ? total.divide(BigDecimal.ONE.add(VAT_RATE), 2, RoundingMode.HALF_UP) : total;
        BigDecimal tax = total.subtract(subtotal);
        LocalDateTime now = LocalDateTime.now().withNano(0);

        Invoice draft = new Invoice(0, payment.id(), now, subtotal, tax, total, "0".repeat(96), "pending");
        int number = invoiceDao.insert(draft);
        Invoice saved = new Invoice(number, payment.id(), now, subtotal, tax, total, invoiceWriter.computeCufe(number, draft), "pending");
        try {
            Path file = invoiceWriter.write(saved, payment, customer);
            Invoice finalInvoice = new Invoice(number, payment.id(), now, subtotal, tax, total, saved.cufe(), file.toString());
            invoiceDao.update(finalInvoice);
            return finalInvoice;
        } catch (IOException e) {
            throw new BusinessRuleException("No se pudo escribir el archivo de la factura: " + e.getMessage());
        }
    }

    // ---------- queries ----------

    public List<Payment> historyOfStudent(int studentId) {
        requireStudent(studentId);
        return paymentDao.findByStudent(studentId);
    }

    public List<Payment> historyOfGuardian(int guardianId) {
        guardianDao.findById(guardianId).orElseThrow(() -> new BusinessRuleException("Acudiente " + guardianId + " no encontrado."));
        return paymentDao.findByGuardian(guardianId);
    }

    /** Pending payments overdue by more than one month. */
    public List<Payment> delinquentPayments() {
        LocalDate limit = LocalDate.now().minusMonths(1);
        return paymentDao.findPending().stream()
                .filter(p -> p.dueDate().isBefore(limit))
                .toList();
    }

    public List<Payment> allPayments() {
        return paymentDao.findAll();
    }
}
