package com.example.app.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import com.example.app.entity.*;
import com.example.app.repo.*;

@Service
public class AutomationService {

    private final SearchJobService searchJobService;
    private final CompanyRepo companyRepo;
    private final CompanyProfileRepo companyProfileRepo;
    private final DataSourceRepo dataSourceRepo;

    public AutomationService(SearchJobService searchJobService, 
                            CompanyRepo companyRepo, 
                            CompanyProfileRepo companyProfileRepo, 
                            DataSourceRepo dataSourceRepo) {
        this.searchJobService = searchJobService;
        this.companyRepo = companyRepo;
        this.companyProfileRepo = companyProfileRepo;
        this.dataSourceRepo = dataSourceRepo;
    }

    @Async
    public void processJob(Long jobId, Long companyId) {
        try {
            // 1. Mark as processing
            searchJobService.updateJobStatus(jobId, SearchJobStatus.PROCESSING);
            
            Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new Exception("Company not found"));

            // 2. Simulate data gathering (Replace with actual web scraper/API later)
            Thread.sleep(5000); 

            // 3. Synthesize and save the profile
            CompanyProfile profile = new CompanyProfile();
            profile.setCompany(company);
            profile.setSector("Technology"); 
            profile.setScale("10-50 Employees"); 
            profile.setProducts("Software Automation");
            profile.setMarket("Vietnam");
            profile.setLastUpdated(LocalDateTime.now());
            companyProfileRepo.save(profile);

            // 4. Save the data source references
            DataSource source = new DataSource();
            source.setCompanyProfile(profile);
            source.setPlatformName("LinkedIn");
            source.setUrl("https://linkedin.com/company/" + company.getDomain());
            source.setRawData("{\"status\": \"success\"}");
            dataSourceRepo.save(source);

            // 5. Mark as complete
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