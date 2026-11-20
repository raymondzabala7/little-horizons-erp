package com.littlehorizons.erp.model;

import java.util.List;

/** Fixed value lists that mirror the ENUM columns of the database. */
public final class Catalog {
    public static final List<String> STUDENT_STATUSES = List.of("Activo", "Graduado", "Retirado", "Suspendido");
    public static final List<String> STUDENT_DOCUMENT_TYPES = List.of("RC", "TI", "CC", "CE", "Pasaporte");
    public static final List<String> ADULT_DOCUMENT_TYPES = List.of("CC", "CE", "Pasaporte");
    public static final List<String> GENDERS = List.of("M", "F", "Otro");
    public static final List<String> PERIODS = List.of("I", "II", "III", "IV", "Final");
    public static final List<String> ENROLLMENT_PERIODS = List.of("I", "II", "III", "IV");
    public static final List<String> ATTENDANCE_STATUSES = List.of("Presente", "Ausente", "Tardanza", "Excusa Médica");
    public static final List<String> WEEK_DAYS = List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");
    public static final List<String> EVENT_TYPES = List.of("Académico", "Cultural", "Deportivo", "Social", "Científico");
    public static final List<String> PLACEMENTS = List.of("1er lugar", "2do lugar", "3er lugar", "Participante");
    public static final List<String> PAYMENT_METHODS = List.of("Efectivo", "Transferencia", "Tarjeta Débito", "Tarjeta Crédito", "Cheque");
    public static final List<String> RESOURCE_STATUSES = List.of("Disponible", "Prestado", "En Mantenimiento", "Dado de Baja");

    private Catalog() { }
}
