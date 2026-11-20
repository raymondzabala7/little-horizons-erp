package com.littlehorizons.erp.model;

/** maxCapacity of 0 means the database holds no limit. */
public record Club(int id, String name, String description, int maxCapacity, int teacherId, String teacherName,
                   boolean active, int activeMembers) {
    public int availableSpots() {
        return maxCapacity == 0 ? Integer.MAX_VALUE : maxCapacity - activeMembers;
    }
}
