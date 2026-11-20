package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.service.ExtracurricularService;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.ExtracurricularView;

public class ExtracurricularController extends BaseController {

    private final ExtracurricularService service;
    private final ExtracurricularView view = new ExtracurricularView();

    public ExtracurricularController(ExtracurricularService service) {
        this.service = service;
    }

    public void run() {
        int option;
        while ((option = view.menu()) != 0) {
            final int selected = option;
            execute(() -> handle(selected));
        }
    }

    private static int id(String label) {
        return InputValidator.readInt(label, 1, Integer.MAX_VALUE);
    }

    private void handle(int option) {
        switch (option) {
            case 1 -> view.showClubs(service.listClubs());
            case 2 -> {
                String name = InputValidator.readText("Nombre del club: ", 100);
                String description = InputValidator.readText("Descripcion: ", 500);
                int capacity = InputValidator.readInt("Cupo maximo: ", 1, 500);
                int teacherId = id("ID del profesor encargado: ");
                view.success("Club creado con ID " + service.createClub(name, description, capacity, teacherId));
            }
            case 3 -> {
                int clubId = id("ID del club: ");
                service.joinClub(clubId, id("ID del estudiante: "));
                view.success("Estudiante inscrito en el club.");
            }
            case 4 -> {
                int clubId = id("ID del club: ");
                service.leaveClub(clubId, id("ID del estudiante: "));
                view.success("Estudiante retirado del club.");
            }
            case 5 -> view.showMembers(service.clubMembers(id("ID del club: ")));
            case 6 -> view.showMembers(service.membershipsOf(id("ID del estudiante: ")));
            case 7 -> view.showEvents(service.listEvents());
            case 8 -> {
                String name = InputValidator.readText("Nombre del evento: ", 150);
                var date = InputValidator.readDate("Fecha del evento");
                String description = InputValidator.readOptionalText("Descripcion (opcional): ", 500);
                String type = InputValidator.readChoice("Tipo: ", Catalog.EVENT_TYPES);
                String place = InputValidator.readOptionalText("Lugar (opcional): ", 150);
                boolean competitive = InputValidator.readYesNo("El evento tiene competencia?");
                view.success("Evento creado con ID " + service.createEvent(name, date, description, type, competitive, place));
            }
            case 9 -> {
                int eventId = id("ID del evento: ");
                int studentId = id("ID del estudiante: ");
                String placement = service.getEvent(eventId).competitive()
                        ? InputValidator.readChoice("Posicion: ", Catalog.PLACEMENTS) : "Participante";
                service.registerParticipation(eventId, studentId, placement);
                view.success("Participacion registrada (" + placement + ").");
            }
            case 10 -> view.showParticipants(service.participants(id("ID del evento: ")));
            default -> view.error("Opcion no valida.");
        }
    }
}
