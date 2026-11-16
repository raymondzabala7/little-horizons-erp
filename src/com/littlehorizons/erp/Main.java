package com.littlehorizons.erp;

import com.littlehorizons.erp.config.DatabaseConnection;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.exception.InputCancelledException;
import com.littlehorizons.erp.view.StudentView;
import com.littlehorizons.erp.view.FinancialView;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Verificando conexión a la base de datos...");
            if (DatabaseConnection.getConnection() != null) {
                AppLogger.info("Aplicación iniciada correctamente.");
                System.out.println("¡Conexión exitosa a la base de datos erp_little_horizons!\n");
            }
        } catch (Exception e) {
            System.err.println("Error crítico al conectar: " + e.getMessage());
            AppLogger.error("Fallo crítico de inicio", e);
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║   ERP Colegio Little Horizons               ║");
            System.out.println("║   Sistema de Gestión Educativa              ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.println("1. Gestión de Estudiantes");
            System.out.println("2. Gestión Académica");
            System.out.println("3. Gestión de Profesores");
            System.out.println("4. Actividades Extracurriculares");
            System.out.println("5. Gestión Financiera (Facturación)");
            System.out.println("6. Gestión de Recursos");
            System.out.println("7. Reportes y Estadísticas");
            System.out.println("8. Salir");
            
            try {
                int option = InputValidator.getInt("Seleccione una opción");
                switch (option) {
                    case 1:
                        StudentView.showMenu();
                        break;
                    case 2:
                        System.out.println("\n[Módulo Académico en desarrollo...]");
                        break;
                    case 3:
                        System.out.println("\n[Módulo de Profesores en desarrollo...]");
                        break;
                    case 4:
                        System.out.println("\n[Módulo Extracurricular en desarrollo...]");
                        break;
                    case 5:
                        FinancialView.showMenu();
                        break;
                    case 6:
                        System.out.println("\n[Módulo de Recursos en desarrollo...]");
                        break;
                    case 7:
                        System.out.println("\n[Reportes y Estadísticas...]");
                        break;
                    case 8:
                        running = false;
                        System.out.println("Saliendo del sistema. ¡Hasta luego!");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (InputCancelledException e) {
                System.out.println("\nOperación cancelada por el usuario. Volviendo al menú principal...");
            }
        }
    }
}
