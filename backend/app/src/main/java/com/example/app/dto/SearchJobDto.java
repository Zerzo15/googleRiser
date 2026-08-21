package com.example.app.dto;

public record SearchJobDto(
    Long id,
    CompanyDto company,
    UserDto requestedBy,
    String status
) {
} 
