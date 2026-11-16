package com.littlehorizons.erp.model;

public record Subject(int id, String name, int grade) {
    public String label() {
        return name + " (" + (grade == 0 ? "Preescolar" : grade + "°") + ")";
    }
}
