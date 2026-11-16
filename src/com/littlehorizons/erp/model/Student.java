package com.littlehorizons.erp.model;

import java.time.LocalDate;

public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private String documentNumber;
    private LocalDate birthDate;
    private String address;
    private String emergencyContact;
    private int gradeLevel;
    private String academicYear;
    private String status;

    public Student() {}

    public Student(int id, String firstName, String lastName, String documentNumber, LocalDate birthDate, String address, String emergencyContact, int gradeLevel, String academicYear, String status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.birthDate = birthDate;
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.gradeLevel = gradeLevel;
        this.academicYear = academicYear;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public int getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(int gradeLevel) { this.gradeLevel = gradeLevel; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
