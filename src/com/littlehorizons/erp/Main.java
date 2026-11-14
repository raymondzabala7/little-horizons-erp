package com.littlehorizons.erp;

import com.littlehorizons.erp.config.DatabaseConnection;
import com.littlehorizons.erp.exception.DatabaseConfigException;
import com.littlehorizons.erp.util.AppLogger;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            System.out.println("Connected to database: " + connection.getCatalog());
            AppLogger.info("Application started and database connection verified");
        } catch (DatabaseConfigException e) {
            System.err.println("Configuration error: " + e.getMessage());
            AppLogger.error("Invalid database configuration", e);
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            AppLogger.error("Database connection failed", e);
        }
    }
}