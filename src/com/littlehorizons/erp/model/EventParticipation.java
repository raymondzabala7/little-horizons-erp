package com.littlehorizons.erp.model;

public record EventParticipation(int id, int eventId, String eventName, int studentId, String studentName,
                                 String placement) { }
