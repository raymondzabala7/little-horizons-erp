package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.AcademicRecord;

import java.math.BigDecimal;
import java.util.List;

public interface AcademicRecordDao {
    void save(int studentId, int subjectId, String period, int year, BigDecimal grade);
    List<AcademicRecord> findByStudent(int studentId);
    List<AcademicRecord> findBySubject(int subjectId, int year);
}
