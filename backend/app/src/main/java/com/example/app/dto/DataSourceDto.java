package com.example.app.dto;

public record DataSourceDto(
    Long id,
    CompanyProfileDto companyProfile,
    String url,
    String platforName
) {
} 
