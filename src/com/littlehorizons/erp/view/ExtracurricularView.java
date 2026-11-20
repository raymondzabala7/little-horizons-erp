package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.EventParticipation;
import com.littlehorizons.erp.model.SchoolEvent;

import java.util.List;

public class ExtracurricularView extends ConsoleView {

    public int menu() {
        return menu("ACTIVIDADES EXTRACURRICULARES", "Listar clubes", "Crear club", "Inscribir estudiante en un club",
                "Retirar estudiante de un club", "Ver miembros de un club", "Clubes de un estudiante",
                "Listar eventos", "Crear evento", "Registrar participacion en un evento", "Ver participantes de un evento");
    }

    public void showClubs(List<Club> clubs) {
        table(List.of("ID", "Club", "Profesor", "Cupo", "Activos", "Libres"),
                clubs.stream().map(c -> row(c.id(), c.name(), c.teacherName(), c.maxCapacity() == 0 ? "sin limite" : c.maxCapacity(),
                        c.activeMembers(), c.maxCapacity() == 0 ? "-" : c.availableSpots())).toList());
    }

    public void showMembers(List<ClubMembership> list) {
        table(List.of("Club", "Estudiante", "Ingreso", "Estado"),
                list.stream().map(m -> row(m.clubName(), m.studentName(), m.joinDate(), m.status())).toList());
    }

    public void showEvents(List<SchoolEvent> events) {
        table(List.of("ID", "Evento", "Fecha", "Tipo", "Lugar", "Competencia"),
                events.stream().map(e -> row(e.id(), e.name(), e.date(), e.type(), e.place(), e.competitive() ? "Si" : "No")).toList());
    }

    public void showParticipants(List<EventParticipation> list) {
        table(List.of("Evento", "Estudiante", "Posicion / rol"),
                list.stream().map(p -> row(p.eventName(), p.studentName(), p.placement())).toList());
    }
}
