package com.littlehorizons.erp.model;

public record Club(int id, String name, String description, int maxCapacity, int teacherId, String teacherName,
                   boolean active, int activeMembers) {
    public int availableSpots() {
        return maxCapacity - activeMembers;
    }
}
