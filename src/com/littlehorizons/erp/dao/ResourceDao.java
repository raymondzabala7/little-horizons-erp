package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceDao {
    List<Resource> findAll();
    Optional<Resource> findById(int id);
    Optional<Resource> findByCode(String code);
    int insert(String code, String name, String category, String location);
    int updateStatus(int resourceId, String status);
}
