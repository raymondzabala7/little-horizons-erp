package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.StudentController;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.exception.InputCancelledException;

import java.time.LocalDate;
import java.util.List;

public class StudentView {
    private static final StudentController controller = new StudentController();

    public static void showMenu() {
        try {
            while (true) {
                System.out.println("\n--- GESTIÓN DE ESTUDIANTES ---");
                System.out.println("1. Registrar Estudiante");
                System.out.println("2. Listar Estudiantes");
                System.out.println("3. Volver al Menú Principal");
                
                int option = InputValidator.getInt("Seleccione una opción");
                if (option == 1) {
                    Student s = new Student();
                    s.setFirstName(InputValidator.getString("Nombre"));
                    s.setLastName(InputValidator.getString("Apellido"));
                    s.setDocumentNumber(InputValidator.getString("Documento"));
                    s.setBirthDate(LocalDate.parse(InputValidator.getString("Fecha de Nacimiento (YYYY-MM-DD)")));
                    s.setAddress(InputValidator.getString("Dirección"));
                    s.setEmergencyContact(InputValidator.getString("Contacto de Emergencia"));
                    s.setGradeLevel(InputValidator.getInt("Grado (1-11)"));
                    s.setAcademicYear(InputValidator.getString("Año Académico (ej. 2026)"));
                    s.setStatus("Activo");

                    if (controller.createStudent(s)) {
                        System.out.println("¡Estudiante registrado con éxito!");
                    } else {
                        System.out.println("Error al registrar el estudiante.");
                    }
                } else if (option == 2) {
                    List<Student> list = controller.listStudents();
                    System.out.println("\n--- LISTA DE ESTUDIANTES (" + list.size() + ") ---");
                    for (Student s : list) {
                        System.out.println("- ID: " + s.getId() + " | " + s.getFirstName() + " " + s.getLastName() + " | Grado: " + s.getGradeLevel());
                    }
                } else if (option == 3) {
                    break;
                }
            }
        } catch (InputCancelledException e) {
            System.out.println("\n[Operación cancelada]");
        }
    }
}
