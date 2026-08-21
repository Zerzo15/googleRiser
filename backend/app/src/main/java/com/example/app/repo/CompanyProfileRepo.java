package com.example.app.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.entity.CompanyProfile;

public interface CompanyProfileRepo extends JpaRepository<CompanyProfile, Long>{
    Optional<CompanyProfile> findByCompanyId(Long companyId);

} 