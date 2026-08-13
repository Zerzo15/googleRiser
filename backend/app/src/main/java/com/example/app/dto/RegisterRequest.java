package com.example.app.dto;

public record RegisterRequest(
    String username,
    String contact,
    String password
) {}
