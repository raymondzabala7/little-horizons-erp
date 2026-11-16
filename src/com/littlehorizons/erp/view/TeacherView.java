package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeachingAssignment;
import com.littlehorizons.erp.util.InputValidator;

import java.util.List;

public class TeacherView extends ConsoleView {

    public int menu() {
        return menu("GESTION DE PROFESORES", "Registrar profesor", "Listar profesores",
                "Asignar materia y horario a un profesor", "Ver carga academica de un profesor");
    }

    public Teacher promptTeacher() {
        info("--- Nuevo profesor ---");
        return new Teacher(0, InputValidator.readText("Nombre completo: ", 120), InputValidator.readDocument("Documento de identidad: "),
                InputValidator.readText("Especializacion: ", 100), InputValidator.readPhone("Telefono: "), InputValidator.readEmail("Correo: "));
    }

    public void showTeachers(List<Teacher> teachers) {
        table(List.of("ID", "Nombre", "Documento", "Especializacion", "Telefono"),
                teachers.stream().map(t -> row(t.id(), t.fullName(), t.documentId(), t.specialization(), t.phone())).toList());
    }

    public void showAssignments(List<TeachingAssignment> list) {
        table(List.of("ID", "Profesor", "Materia", "Dia", "Inicio", "Fin"),
                list.stream().map(a -> row(a.id(), a.teacherName(), a.subjectName(), a.day(), a.start(), a.end())).toList());
    }
}
