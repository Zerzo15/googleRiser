package com.example.app.service;

import org.springframework.stereotype.Service;

import com.example.app.dto.CompanyDto;
import com.example.app.entity.Company;
import com.example.app.entity.CompanyProfile;
import com.example.app.repo.CompanyProfileRepo;
import com.example.app.repo.CompanyRepo;

@Service
public class CompanyService {
    private final CompanyProfileRepo companyProfileRepo;
    private final CompanyRepo companyRepo;

    public CompanyService(CompanyRepo companyRepo, CompanyProfileRepo companyProfileRepo) {
        this.companyRepo = companyRepo;
        this.companyProfileRepo = companyProfileRepo;
    }

    public CompanyDto registerCompany(Company company, String username) throws Exception{

        String safeName = company.getName();
        String safeDomain = company.getDomain();

        if (safeName == null || safeName.trim().isEmpty()) {
            throw new Exception("Error: Enter Company Name");
        }

        if (companyRepo.existsByName(safeName) && companyRepo.existsByDomain(safeDomain)) {
            throw new Exception("Error: Company Already Registed");
        }

        Company savedCompany = companyRepo.save(company);

        return new CompanyDto(
            savedCompany.getId(),
            safeName,
            safeDomain
        );
    }

    public CompanyProfile getProfile(Long companyId) throws Exception {
        return companyProfileRepo.findByCompanyId(companyId)
            .orElseThrow(() -> new Exception("Error: Company Profile Not Found"));
    }
}
