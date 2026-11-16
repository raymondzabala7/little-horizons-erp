package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Guardian;

import java.util.List;
import java.util.Optional;

public interface GuardianDao {
    List<Guardian> findAll();
    Optional<Guardian> findById(int id);
    Optional<Guardian> findByDocument(String documentId);
    int insert(Guardian guardian);
}
