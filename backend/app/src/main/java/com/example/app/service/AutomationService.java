package com.example.app.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.app.dto.AiCompanyAnalysisDto;
import com.example.app.entity.Company;
import com.example.app.entity.CompanyProfile;
import com.example.app.entity.DataSource;
import com.example.app.entity.SearchJobStatus;
import com.example.app.repo.CompanyProfileRepo;
import com.example.app.repo.CompanyRepo;
import com.example.app.repo.DataSourceRepo;

@Service
public class AutomationService {

    private final SearchJobService searchJobService;
    private final CompanyRepo companyRepo;
    private final CompanyProfileRepo companyProfileRepo;
    private final DataSourceRepo dataSourceRepo;
    private final CompanyIntelligenceService intelligenceService;

    public AutomationService(
            SearchJobService searchJobService,
            CompanyRepo companyRepo,
            CompanyProfileRepo companyProfileRepo,
            DataSourceRepo dataSourceRepo,
            CompanyIntelligenceService intelligenceService) {
        this.searchJobService = searchJobService;
        this.companyRepo = companyRepo;
        this.companyProfileRepo = companyProfileRepo;
        this.dataSourceRepo = dataSourceRepo;
        this.intelligenceService = intelligenceService;
    }

    @Async
    public void processJob(Long jobId, Long companyId) {
        try {
            searchJobService.updateJobStatus(jobId, SearchJobStatus.PROCESSING);

            Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

            AiCompanyAnalysisDto analysis = intelligenceService.analyzeCompany(
                company.getName(),
                company.getDomain()
            );

            if (analysis.taxId() != null && !analysis.taxId().isBlank()) {
                company.setTaxId(analysis.taxId());
                companyRepo.save(company);
            }

            CompanyProfile profile = companyProfileRepo.findByCompanyId(companyId)
                .orElseGet(CompanyProfile::new);

            profile.setCompany(company);
            profile.setSector(analysis.sector());
            profile.setScale(analysis.scale());
            profile.setProducts(analysis.products());
            profile.setMarket(analysis.market());
            profile.setLastUpdated(LocalDateTime.now());
            CompanyProfile savedProfile = companyProfileRepo.save(profile);

            if (analysis.sources() != null) {
                for (var sourceItem : analysis.sources()) {
                    DataSource source = new DataSource();
                    source.setCompanyProfile(savedProfile);
                    source.setPlatformName(sourceItem.platformName());
                    source.setUrl(sourceItem.url());
                    source.setRawData(sourceItem.snippet());
                    dataSourceRepo.save(source);
                }
            }

            searchJobService.updateJobStatus(jobId, SearchJobStatus.COMPLETE);

        } catch (Exception e) {
            try {
                searchJobService.updateJobStatus(jobId, SearchJobStatus.FAILED);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}