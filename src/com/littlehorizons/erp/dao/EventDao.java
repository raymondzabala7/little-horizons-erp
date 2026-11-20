package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.EventParticipation;
import com.littlehorizons.erp.model.SchoolEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventDao {
    List<SchoolEvent> findAll();
    Optional<SchoolEvent> findById(int id);
    int insert(String name, LocalDate date, String description, String type, boolean competitive, String place);
    void saveParticipation(int eventId, int studentId, String role, String placement);
    List<EventParticipation> findParticipants(int eventId);
    List<EventParticipation> findAllParticipations();
}
