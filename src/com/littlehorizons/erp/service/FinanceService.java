package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.GuardianDao;
import com.littlehorizons.erp.dao.InvoiceDao;
import com.littlehorizons.erp.dao.Jdbc;
import com.littlehorizons.erp.dao.PaymentDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.exception.BusinessRuleException;
import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.model.Delinquent;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Payment concepts, payments, delinquency and electronic (text) invoices.
 * The payments table only stores payments made, so delinquency is computed from the monthly
 * tuition (PENSION) months that have no payment.
 */
public class FinanceService {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.19");
    /** Education services are VAT exempt; these concept codes are charged with 19% VAT included. */
    private static final Set<String> TAXABLE_CODES = Set.of("UNIFORME");
    private static final String TUITION_CODE = "PENSION";
    private static final int FIRST_SCHOOL_MONTH = 2;
    private static final int LAST_SCHOOL_MONTH = 11;
    private static final int DUE_DAY = 5;

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

    // ---------- concepts and custom values ----------

    public List<PaymentConcept> listConcepts() {
        return paymentDao.findConcepts();
    }

    public int createConcept(String code, String name, BigDecimal baseValue, boolean recurring) {
        return paymentDao.insertConcept(code.toUpperCase(), name, baseValue, recurring);
    }

    public PaymentConcept getConcept(int id) {
        return paymentDao.findConceptById(id).orElseThrow(() -> new BusinessRuleException("Concepto " + id + " no encontrado."));
    }

    public void setCustomValue(int studentId, int conceptId, int year, BigDecimal value, String reason) {
        requireStudent(studentId);
        getConcept(conceptId);
        paymentDao.saveCustomValue(studentId, conceptId, year, value, reason);
    }

    private Student requireStudent(int studentId) {
        return studentDao.findById(studentId).orElseThrow(() -> new BusinessRuleException("Estudiante " + studentId + " no encontrado."));
    }

    private BigDecimal valueFor(int studentId, PaymentConcept concept, int year) {
        return paymentDao.findCustomValue(studentId, concept.id(), year).orElse(concept.baseValue());
    }

    private static String periodOf(int month) {
        return month <= 4 ? "I" : month <= 7 ? "II" : month <= 9 ? "III" : "IV";
    }

    // ---------- payments ----------

    /**
     * Registers a payment and issues its invoice in one transaction. Monthly concepts need the month they cover;
     * the amount is the student's custom value for the year when there is one, otherwise the base value.
     */
    public Invoice registerPayment(int studentId, int conceptId, String method, Integer month, int year) {
        if (!Catalog.PAYMENT_METHODS.contains(method)) {
            throw new BusinessRuleException("Metodo de pago invalido: " + method);
        }
        Student student = requireStudent(studentId);
        if (!"Activo".equals(student.status())) {
            throw new BusinessRuleException("El estudiante no esta activo (estado: " + student.status() + ").");
        }
        PaymentConcept concept = getConcept(conceptId);
        if (concept.recurring() && month == null) {
            throw new BusinessRuleException("El concepto '" + concept.name() + "' es mensual: indique el mes que cubre.");
        }
        Integer appliedMonth = concept.recurring() ? month : null;
        if (appliedMonth != null && paymentDao.isMonthPaid(studentId, conceptId, year, appliedMonth)) {
            throw new BusinessRuleException("El mes " + appliedMonth + "/" + year + " de '" + concept.name() + "' ya esta pagado.");
        }
        BigDecimal amount = valueFor(studentId, concept, year);
        String period = appliedMonth == null ? "Anual" : periodOf(appliedMonth);
        return Jdbc.transaction(() -> {
            String receipt = paymentDao.nextReceiptNumber();
            int paymentId = paymentDao.insertPayment(studentId, conceptId, amount, LocalDate.now(), method, receipt, year, period, appliedMonth);
            return issueInvoice(getPayment(paymentId));
        });
    }

    public Payment getPayment(int paymentId) {
        return paymentDao.findById(paymentId).orElseThrow(() -> new BusinessRuleException("Pago " + paymentId + " no encontrado."));
    }

    public List<Payment> paymentsWithoutInvoice() {
        return paymentDao.findWithoutInvoice();
    }

    /** Issues the invoice of an existing payment, or returns the one already issued. */
    public Invoice generateInvoice(int paymentId) {
        Payment payment = getPayment(paymentId);
        return invoiceDao.findByPayment(paymentId)
                .map(existing -> withAmounts(existing, payment))
                .orElseGet(() -> Jdbc.transaction(() -> issueInvoice(payment)));
    }

    private Invoice withAmounts(Invoice base, Payment payment) {
        BigDecimal total = payment.amount();
        BigDecimal subtotal = subtotalOf(payment);
        return new Invoice(base.id(), base.number(), base.paymentId(), base.issuedAt(), subtotal, total.subtract(subtotal), total, base.cufe(), base.filePath());
    }

    private BigDecimal subtotalOf(Payment payment) {
        return TAXABLE_CODES.contains(payment.conceptCode())
                ? payment.amount().divide(BigDecimal.ONE.add(VAT_RATE), 2, RoundingMode.HALF_UP) : payment.amount();
    }

    private Invoice issueInvoice(Payment payment) {
        Guardian customer = guardianDao.findPrincipalOfStudent(payment.studentId())
                .orElseThrow(() -> new BusinessRuleException("El estudiante no tiene acudiente a quien facturar."));
        LocalDateTime now = LocalDateTime.now().withNano(0);
        BigDecimal total = payment.amount();
        BigDecimal subtotal = subtotalOf(payment);
        int id = invoiceDao.insertDraft(payment.id(), "TMP-" + payment.id() + "-" + System.nanoTime(), now.toLocalDate());
        String number = String.format("LH-%06d", id);
        Invoice draft = new Invoice(id, number, payment.id(), now, subtotal, total.subtract(subtotal), total, "0".repeat(96), "pending");
        Invoice signed = new Invoice(id, number, payment.id(), now, subtotal, total.subtract(subtotal), total, invoiceWriter.computeCufe(number, draft), "pending");
        try {
            Path file = invoiceWriter.write(signed, payment, customer);
            Invoice done = new Invoice(id, number, payment.id(), now, subtotal, total.subtract(subtotal), total, signed.cufe(), file.toString());
            invoiceDao.complete(done, file.getFileName().toString());
            return done;
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

    /**
     * Active students with tuition months of the current year (February to November) whose due date
     * (day 5) is more than one month in the past and that have no tuition payment.
     */
    public List<Delinquent> delinquents() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        PaymentConcept tuition = paymentDao.findConcepts().stream()
                .filter(c -> TUITION_CODE.equals(c.code())).findFirst()
                .orElseThrow(() -> new BusinessRuleException("No existe el concepto de pension (codigo " + TUITION_CODE + ")."));
        List<Integer> overdueMonths = IntStream.rangeClosed(FIRST_SCHOOL_MONTH, LAST_SCHOOL_MONTH)
                .filter(m -> LocalDate.of(year, m, DUE_DAY).isBefore(today.minusMonths(1)))
                .boxed().toList();
        Set<String> paid = paymentDao.findPaidMonths(tuition.id(), year).stream()
                .map(p -> p[0] + "-" + p[1]).collect(Collectors.toCollection(HashSet::new));
        return studentDao.findAll().stream()
                .filter(s -> "Activo".equals(s.status()) && s.enrollmentYear() == year)
                .map(s -> {
                    List<Integer> missing = overdueMonths.stream().filter(m -> !paid.contains(s.id() + "-" + m)).toList();
                    BigDecimal due = valueFor(s.id(), tuition, year).multiply(BigDecimal.valueOf(missing.size()));
                    return new Delinquent(s.id(), s.fullName(), s.gradeLabel(), missing, due);
                })
                .filter(d -> !d.overdueMonths().isEmpty())
                .sorted(Comparator.comparingInt((Delinquent d) -> d.overdueMonths().size()).reversed())
                .toList();
    }

    public List<Payment> allPayments() {
        return paymentDao.findAll();
    }
}
