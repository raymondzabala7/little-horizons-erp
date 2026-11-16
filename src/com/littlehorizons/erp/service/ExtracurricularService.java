package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.ClubDao;
import com.littlehorizons.erp.dao.EventDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.dao.TeacherDao;
import com.littlehorizons.erp.exception.BusinessRuleException;
import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.EventParticipation;
import com.littlehorizons.erp.model.SchoolEvent;
import com.littlehorizons.erp.model.Student;

import java.time.LocalDate;
import java.util.List;

/** Clubs and school events. */
public class ExtracurricularService {

    private final ClubDao clubDao;
    private final EventDao eventDao;
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;

    public ExtracurricularService(ClubDao clubDao, EventDao eventDao, StudentDao studentDao, TeacherDao teacherDao) {
        this.clubDao = clubDao;
        this.eventDao = eventDao;
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
    }

    // ---------- clubs ----------

    public List<Club> listClubs() {
        return clubDao.findAll();
    }

    public Club getClub(int id) {
        return clubDao.findById(id).orElseThrow(() -> new BusinessRuleException("Club " + id + " no encontrado."));
    }

    public int createClub(String name, String description, int capacity, int teacherId) {
        if (teacherDao.findById(teacherId).isEmpty()) {
            throw new BusinessRuleException("Profesor " + teacherId + " no encontrado.");
        }
        return clubDao.insert(name, description, capacity, teacherId);
    }

    public void joinClub(int clubId, int studentId) {
        Club club = getClub(clubId);
        Student student = studentDao.findById(studentId)
                .orElseThrow(() -> new BusinessRuleException("Estudiante " + studentId + " no encontrado."));
        if (!club.active()) {
            throw new BusinessRuleException("El club no esta activo.");
        }
        if (!"Activo".equals(student.status())) {
            throw new BusinessRuleException("Solo los estudiantes activos pueden inscribirse en un club.");
        }
        if (clubDao.isActiveMember(clubId, studentId)) {
            throw new BusinessRuleException("El estudiante ya es miembro activo de este club.");
        }
        if (club.availableSpots() <= 0) {
            throw new BusinessRuleException("El club esta lleno (cupo " + club.maxCapacity() + ").");
        }
        clubDao.addMember(clubId, studentId, LocalDate.now());
    }

    public void leaveClub(int clubId, int studentId) {
        if (clubDao.withdrawMember(clubId, studentId) == 0) {
            throw new BusinessRuleException("El estudiante no es miembro activo de este club.");
        }
    }

    public List<ClubMembership> clubMembers(int clubId) {
        getClub(clubId);
        return clubDao.findMembers(clubId);
    }

    public List<ClubMembership> membershipsOf(int studentId) {
        return clubDao.findMembershipsByStudent(studentId);
    }

    // ---------- events ----------

    public List<SchoolEvent> listEvents() {
        return eventDao.findAll();
    }

    public SchoolEvent getEvent(int id) {
        return eventDao.findById(id).orElseThrow(() -> new BusinessRuleException("Evento " + id + " no encontrado."));
    }

    public int createEvent(String name, LocalDate date, String description, String type, boolean competitive) {
        return eventDao.insert(name, date, description, type, competitive);
    }

    public void registerParticipation(int eventId, int studentId, String placement) {
        SchoolEvent event = getEvent(eventId);
        if (studentDao.findById(studentId).isEmpty()) {
            throw new BusinessRuleException("Estudiante " + studentId + " no encontrado.");
        }
        if (!event.competitive() && !"Participante".equals(placement)) {
            throw new BusinessRuleException("El evento no tiene competencia; la posicion debe ser 'Participante'.");
        }
        eventDao.saveParticipation(eventId, studentId, placement);
    }

    public List<EventParticipation> participants(int eventId) {
        getEvent(eventId);
        return eventDao.findParticipants(eventId);
    }
}
