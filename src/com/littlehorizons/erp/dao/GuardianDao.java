package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.GuardianForm;

import java.util.List;
import java.util.Optional;

public interface GuardianDao {
    List<Guardian> findAll();
    Optional<Guardian> findById(int id);
    Optional<Guardian> findByDocument(String documentId);
    /** Principal guardian of a student (or any linked guardian when none is flagged as principal). */
    Optional<Guardian> findPrincipalOfStudent(int studentId);
    int insert(GuardianForm form);
}
