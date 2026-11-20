package com.littlehorizons.erp.model;

public record GuardianForm(PersonName name, String documentType, String documentId, String relationship,
                           String phone, String email, String address) { }
