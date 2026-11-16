package com.littlehorizons.erp;

import com.littlehorizons.erp.config.DatabaseConnection;
import com.littlehorizons.erp.controller.MainController;
import com.littlehorizons.erp.exception.DatabaseConfigException;
import com.littlehorizons.erp.exception.InputClosedException;
import com.littlehorizons.erp.util.AppLogger;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.open()) {
            System.out.println("Conectado a la base de datos: " + connection.getCatalog());
            AppLogger.info("Application started and database connection verified");
        } catch (DatabaseConfigException e) {
            System.err.println("Error de configuracion: " + e.getMessage());
            AppLogger.error("Invalid database configuration", e);
            return;
        } catch (SQLException e) {
            System.err.println("No se pudo conectar a la base de datos: " + e.getMessage());
            AppLogger.error("Database connection failed", e);
            return;
        }
        try {
            new MainController().run();
        } catch (InputClosedException e) {
            System.out.println("\nEntrada cerrada. Fin del programa.");
        }
        AppLogger.info("Application closed normally");
    }
}
