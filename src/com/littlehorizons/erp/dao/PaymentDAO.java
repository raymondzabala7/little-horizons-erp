package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.config.DatabaseConnection;
import com.littlehorizons.erp.util.AppLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    public boolean insert(Payment payment) {
        String sql = "INSERT INTO pagos (estudiante_id, concepto, monto, fecha_pago, metodo_pago, recibo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, payment.getStudentId());
            stmt.setString(2, payment.getPaymentConcept());
            stmt.setDouble(3, payment.getAmount());
            stmt.setDate(4, Date.valueOf(payment.getPaymentDate()));
            stmt.setString(5, payment.getPaymentMethod());
            stmt.setString(6, payment.getReceiptNumber());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            AppLogger.error("Error inserting payment", e);
            return false;
        }
    }

    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM pagos";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Payment p = new Payment(
                    rs.getInt("id"),
                    rs.getInt("estudiante_id"),
                    rs.getString("concepto"),
                    rs.getDouble("monto"),
                    rs.getDate("fecha_pago").toLocalDate(),
                    rs.getString("metodo_pago"),
                    rs.getString("recibo")
                );
                payments.add(p);
            }
        } catch (SQLException e) {
            AppLogger.error("Error finding payments", e);
        }
        return payments;
    }
}
