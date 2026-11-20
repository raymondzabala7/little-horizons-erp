package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.AcademicGrade;

import java.util.List;

public interface AcademicGradeDao {
    /** Every academic record joined with its subject (id, code and name). */
    List<AcademicGrade> findAll();
}
