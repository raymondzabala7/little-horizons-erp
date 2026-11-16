package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.InputValidator;

import java.time.LocalDate;
import java.util.List;

public class StudentView extends ConsoleView {

    public int menu() {
        return menu("GESTION DE ESTUDIANTES", "Registrar acudiente", "Registrar estudiante", "Listar estudiantes",
                "Buscar estudiante por nombre", "Ver detalle de un estudiante", "Cambiar estado de matricula",
                "Listar acudientes", "Estudiantes de un acudiente");
    }

    public Guardian promptGuardian() {
        info("--- Nuevo acudiente ---");
        return new Guardian(0, InputValidator.readText("Nombre completo: ", 120), InputValidator.readDocument("Documento de identidad: "),
                InputValidator.readPhone("Telefono: "), InputValidator.readEmail("Correo: "), InputValidator.readText("Direccion: ", 150));
    }

    public Student promptStudent() {
        info("--- Nuevo estudiante ---");
        int guardianId = InputValidator.readInt("ID del acudiente (opcion 7 del menu para consultarlos): ", 1, Integer.MAX_VALUE);
        String name = InputValidator.readText("Nombre completo: ", 120);
        String document = InputValidator.readDocument("Documento de identidad: ");
        LocalDate birth = InputValidator.readDate("Fecha de nacimiento");
        String address = InputValidator.readText("Direccion: ", 150);
        String emergency = InputValidator.readText("Contacto de emergencia (nombre y telefono): ", 100);
        int grade = InputValidator.readInt("Grado (0 = Preescolar, 1 a 11): ", 0, 11);
        int year = InputValidator.readInt("Anio de matricula: ", 2000, LocalDate.now().getYear() + 1);
        return new Student(0, name, document, birth, address, emergency, guardianId, grade, year, "Activo");
    }

    public void showStudents(List<Student> students) {
        table(List.of("ID", "Nombre", "Documento", "Grado", "Matricula", "Estado"),
                students.stream().map(s -> row(s.id(), s.fullName(), s.documentId(), s.gradeLabel(), s.enrollmentYear(), s.status())).toList());
    }

    public void showGuardians(List<Guardian> guardians) {
        table(List.of("ID", "Nombre", "Documento", "Telefono", "Correo"),
                guardians.stream().map(g -> row(g.id(), g.fullName(), g.documentId(), g.phone(), g.email())).toList());
    }

    public void showDetail(Student s, Guardian g) {
        banner("ESTUDIANTE #" + s.id(), null);
        info("Nombre            : " + s.fullName());
        info("Documento         : " + s.documentId());
        info("Nacimiento        : " + s.birthDate());
        info("Direccion         : " + s.address());
        info("Contacto emerg.   : " + s.emergencyContact());
        info("Grado / Matricula : " + s.gradeLabel() + " / " + s.enrollmentYear() + " (" + s.status() + ")");
        info("Acudiente         : " + g.fullName() + " - " + g.phone() + " - " + g.email());
    }
}
