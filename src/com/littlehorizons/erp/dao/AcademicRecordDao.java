package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.AcademicRecord;

import java.math.BigDecimal;
import java.util.List;

public interface AcademicRecordDao {
    /** Inserts the grade or updates it when that student/subject/period/year already has one. */
    void save(int studentId, int subjectId, int teacherId, String period, int year, BigDecimal grade);
    List<AcademicRecord> findByStudent(int studentId);
    List<AcademicRecord> findBySubject(int subjectId, int year);
}
