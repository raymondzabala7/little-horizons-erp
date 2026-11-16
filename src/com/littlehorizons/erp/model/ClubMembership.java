package com.littlehorizons.erp.model;

import java.time.LocalDate;

public record ClubMembership(int id, int clubId, String clubName, int studentId, String studentName,
                             LocalDate joinDate, String status) { }
