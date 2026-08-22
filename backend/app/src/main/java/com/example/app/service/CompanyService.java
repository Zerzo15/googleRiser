package com.example.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app.dto.CompanyDto;
import com.example.app.dto.DataSourceDto;
import com.example.app.entity.Company;
import com.example.app.entity.CompanyProfile;
import com.example.app.repo.CompanyProfileRepo;
import com.example.app.repo.CompanyRepo;
import com.example.app.repo.DataSourceRepo;

@Service
public class CompanyService {
    private final CompanyProfileRepo companyProfileRepo;
    private final CompanyRepo companyRepo;
    private final DataSourceRepo dataSourceRepo;

    public CompanyService(CompanyRepo companyRepo, CompanyProfileRepo companyProfileRepo, DataSourceRepo dataSourceRepo) {
        this.companyRepo = companyRepo;
        this.companyProfileRepo = companyProfileRepo;
        this.dataSourceRepo = dataSourceRepo;
    }

    public CompanyDto registerCompany(Company company, String username) throws Exception{

        String safeName = company.getName();
        String safeDomain = company.getDomain();

        if (safeName == null || safeName.trim().isEmpty()) {
            throw new Exception("Error: Enter Company Name");
        }

        if (companyRepo.existsByName(safeName)
                || (safeDomain != null && !safeDomain.trim().isEmpty() && companyRepo.existsByDomain(safeDomain))) {
            throw new Exception("Error: Company Already Registered");
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

    public List<DataSourceDto> getSources(Long companyId) throws Exception {
        CompanyProfile profile = getProfile(companyId);
        return dataSourceRepo.findByCompanyProfileId(profile.getId()).stream()
            .map(source -> new DataSourceDto(source.getId(), source.getUrl(), source.getPlatformName()))
            .toList();
    }
}
