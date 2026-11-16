package com.littlehorizons.erp.model;

import java.time.LocalDate;

public record SchoolEvent(int id, String name, LocalDate date, String description, String type, boolean competitive) { }
