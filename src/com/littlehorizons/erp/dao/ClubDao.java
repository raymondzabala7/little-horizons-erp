package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClubDao {
    List<Club> findAll();
    Optional<Club> findById(int id);
    int insert(String name, String description, int maxCapacity, int teacherId);
    boolean isActiveMember(int clubId, int studentId);
    void addMember(int clubId, int studentId, LocalDate joinDate);
    int withdrawMember(int clubId, int studentId);
    List<ClubMembership> findMembers(int clubId);
    List<ClubMembership> findMembershipsByStudent(int studentId);
}
