package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherForm;
import com.littlehorizons.erp.model.TeachingAssignment;
import com.littlehorizons.erp.util.InputValidator;

import java.util.List;

public class TeacherView extends ConsoleView {

    public int menu() {
        return menu("GESTION DE PROFESORES", "Registrar profesor", "Listar profesores",
                "Asignar materia y horario a un profesor", "Ver carga academica de un profesor");
    }

    public TeacherForm promptTeacher() {
        info("--- Nuevo profesor ---");
        var name = promptName();
        String docType = InputValidator.readChoice("Tipo de documento: ", Catalog.ADULT_DOCUMENT_TYPES);
        String document = InputValidator.readDocument("Numero de documento: ");
        return new TeacherForm(name, docType, document, InputValidator.readEmail("Correo: "),
                InputValidator.readPhone("Telefono: "), InputValidator.readText("Especializacion: ", 100));
    }

    public void showTeachers(List<Teacher> teachers) {
        table(List.of("ID", "Nombre", "Documento", "Especializacion", "Telefono", "Estado"),
                teachers.stream().map(t -> row(t.id(), t.fullName(), t.documentId(), t.specialization(), t.phone(), t.status())).toList());
    }

    public void showAssignments(List<TeachingAssignment> list) {
        table(List.of("Asig.", "Profesor", "Materia", "Anio", "Seccion", "Dia", "Inicio", "Fin"),
                list.stream().map(a -> row(a.id(), a.teacherName(), a.subjectName(), a.year(), a.section(), a.day(), a.start(), a.end())).toList());
    }
}
