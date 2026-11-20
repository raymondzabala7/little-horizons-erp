package com.littlehorizons.erp.model;

public record Guardian(int id, String fullName, String documentType, String documentId, String relationship,
                       String phone, String email, String address) { }
