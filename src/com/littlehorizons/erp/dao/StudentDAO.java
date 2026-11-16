package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.config.DatabaseConnection;
import com.littlehorizons.erp.util.AppLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    public boolean insert(Student student) {
        String sql = "INSERT INTO estudiantes (nombre, apellido, documento, fecha_nacimiento, direccion, contacto_emergencia, grado, anio_academico, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getDocumentNumber());
            stmt.setDate(4, Date.valueOf(student.getBirthDate()));
            stmt.setString(5, student.getAddress());
            stmt.setString(6, student.getEmergencyContact());
            stmt.setInt(7, student.getGradeLevel());
            stmt.setString(8, student.getAcademicYear());
            stmt.setString(9, student.getStatus());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            AppLogger.error("Error inserting student", e);
            return false;
        }
    }

    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM estudiantes";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Student s = new Student(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("documento"),
                    rs.getDate("fecha_nacimiento").toLocalDate(),
                    rs.getString("direccion"),
                    rs.getString("contacto_emergencia"),
                    rs.getInt("grado"),
                    rs.getString("anio_academico"),
                    rs.getString("estado")
                );
                students.add(s);
            }
        } catch (SQLException e) {
            AppLogger.error("Error finding all students", e);
        }
        return students;
    }
}
