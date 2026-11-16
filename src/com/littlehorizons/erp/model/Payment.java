package com.littlehorizons.erp.model;

import java.time.LocalDate;

public class Payment {
    private int id;
    private int studentId;
    private String paymentConcept;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String receiptNumber;

    public Payment() {}

    public Payment(int id, int studentId, String paymentConcept, double amount, LocalDate paymentDate, String paymentMethod, String receiptNumber) {
        this.id = id;
        this.studentId = studentId;
        this.paymentConcept = paymentConcept;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getPaymentConcept() { return paymentConcept; }
    public void setPaymentConcept(String paymentConcept) { this.paymentConcept = paymentConcept; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
}
