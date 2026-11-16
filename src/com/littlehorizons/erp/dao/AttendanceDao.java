package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceDao {
    void save(int studentId, int subjectId, LocalDate date, String status);
    List<Attendance> findBetween(LocalDate from, LocalDate to);
    List<Attendance> findByStudent(int studentId);
}
