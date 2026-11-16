package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.PaymentDAO;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.util.InvoiceGenerator;
import java.util.List;

public class PaymentService {
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public boolean registerPayment(Payment payment, String studentName, String parentName) {
        boolean saved = paymentDAO.insert(payment);
        if (saved) {
            InvoiceGenerator.generateElectronicInvoice(payment, studentName, parentName);
        }
        return saved;
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.findAll();
    }
}
