package com.littlehorizons.erp.model;

import java.util.List;

/** Fixed value lists that mirror the ENUM columns of the database. */
public final class Catalog {
    public static final List<String> STUDENT_STATUSES = List.of("Activo", "Graduado", "Retirado");
    public static final List<String> PERIODS = List.of("I", "II", "III", "IV", "Final");
    public static final List<String> ATTENDANCE_STATUSES = List.of("Presente", "Ausente", "Tardanza", "Excusa Medica");
    public static final List<String> WEEK_DAYS = List.of("Lunes", "Martes", "Miercoles", "Jueves", "Viernes");
    public static final List<String> EVENT_TYPES = List.of("Academico", "Cultural", "Deportivo", "Social");
    public static final List<String> PLACEMENTS = List.of("1er lugar", "2do lugar", "3er lugar", "Participante");
    public static final List<String> PAYMENT_METHODS = List.of("Efectivo", "Transferencia", "Tarjeta");
    public static final List<String> RESOURCE_CATEGORIES =
            List.of("Libro", "Equipo de Laboratorio", "Equipo Deportivo", "Dispositivo Tecnologico");
    public static final List<String> RESOURCE_STATUSES =
            List.of("Disponible", "Prestado", "En Mantenimiento", "Dado de Baja");

    private Catalog() { }
}
