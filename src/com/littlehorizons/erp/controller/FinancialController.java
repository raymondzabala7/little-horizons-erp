package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.service.PaymentService;
import java.util.List;

public class FinancialController {
    private final PaymentService paymentService = new PaymentService();

    public boolean processPayment(Payment payment, String studentName, String parentName) {
        return paymentService.registerPayment(payment, studentName, parentName);
    }

    public List<Payment> listPayments() {
        return paymentService.getAllPayments();
    }
}
