package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.GuardianForm;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.StudentForm;
import com.littlehorizons.erp.util.InputValidator;

import java.time.LocalDate;
import java.util.List;

public class StudentView extends ConsoleView {

    public int menu() {
        return menu("GESTION DE ESTUDIANTES", "Registrar acudiente", "Registrar estudiante (con matricula)", "Listar estudiantes",
                "Buscar estudiante por nombre", "Ver detalle de un estudiante", "Cambiar estado del estudiante",
                "Listar acudientes", "Estudiantes de un acudiente");
    }

    public GuardianForm promptGuardian() {
        info("--- Nuevo acudiente ---");
        var name = promptName();
        String docType = InputValidator.readChoice("Tipo de documento: ", Catalog.ADULT_DOCUMENT_TYPES);
        String document = InputValidator.readDocument("Numero de documento: ");
        String relationship = InputValidator.readText("Parentesco (Padre, Madre, Tutor Legal...): ", 50);
        return new GuardianForm(name, docType, document, relationship, InputValidator.readPhone("Telefono: "),
                InputValidator.readEmail("Correo: "), InputValidator.readText("Direccion: ", 255));
    }

    public StudentForm promptStudent() {
        info("--- Nuevo estudiante ---");
        int guardianId = InputValidator.readInt("ID del acudiente principal (opcion 7 del menu para consultarlos): ", 1, Integer.MAX_VALUE);
        var name = promptName();
        String docType = InputValidator.readChoice("Tipo de documento: ", Catalog.STUDENT_DOCUMENT_TYPES);
        String document = InputValidator.readDocument("Numero de documento: ");
        LocalDate birth = InputValidator.readDate("Fecha de nacimiento");
        String gender = InputValidator.readChoice("Genero: ", Catalog.GENDERS);
        String address = InputValidator.readText("Direccion: ", 255);
        int grade = InputValidator.readInt("Grado (0 = Preescolar, 1 a 11): ", 0, 11);
        int year = InputValidator.readInt("Anio de matricula: ", 2000, LocalDate.now().getYear() + 1);
        return new StudentForm(name, docType, document, birth, gender, address, guardianId, grade, year);
    }

    public void showStudents(List<Student> students) {
        table(List.of("ID", "Nombre", "Documento", "Grado", "Matricula", "Estado"),
                students.stream().map(s -> row(s.id(), s.fullName(), s.documentId(), s.gradeLabel(),
                        s.enrollmentYear() == 0 ? "-" : s.enrollmentYear(), s.status())).toList());
    }

    public void showGuardians(List<Guardian> guardians) {
        table(List.of("ID", "Nombre", "Documento", "Parentesco", "Telefono", "Correo"),
                guardians.stream().map(g -> row(g.id(), g.fullName(), g.documentId(), g.relationship(), g.phone(), g.email())).toList());
    }

    public void showDetail(Student s, Guardian g) {
        banner("ESTUDIANTE #" + s.id(), null);
        info("Nombre            : " + s.fullName());
        info("Documento         : " + s.documentType() + " " + s.documentId());
        info("Nacimiento        : " + s.birthDate());
        info("Direccion         : " + s.address());
        info("Grado / Matricula : " + s.gradeLabel() + " / " + s.enrollmentYear() + " (" + s.status() + ")");
        info("Acudiente         : " + g.fullName() + " (" + g.relationship() + ") - " + g.phone() + " - " + g.email());
    }
}
