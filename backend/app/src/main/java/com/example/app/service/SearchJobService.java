package com.example.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app.entity.Company;
import com.example.app.entity.SearchJob;
import com.example.app.entity.SearchJobStatus;
import com.example.app.entity.User;
import com.example.app.dto.CompanyDto;
import com.example.app.dto.SearchJobDto;
import com.example.app.dto.UserDto;
import com.example.app.repo.CompanyRepo;
import com.example.app.repo.SearchJobRepo;
import com.example.app.repo.UserRepo;

@Service
public class SearchJobService {
    private final SearchJobRepo searchJobRepo;
    private final UserRepo userRepo;
    private final CompanyRepo companyRepo;

    public SearchJobService(SearchJobRepo searchJobRepo, UserRepo userRepo, CompanyRepo companyRepo) {
        this.searchJobRepo = searchJobRepo;
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;
    }

    public SearchJob initializeJob(Long companyId, String username) throws Exception {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new Exception("User not found"));
        
        Company company = companyRepo.findById(companyId)
            .orElseThrow(() -> new Exception("Company not found"));

        SearchJob job = new SearchJob();
        job.setCompany(company);
        job.setRequestedBy(user);
        job.setStatus(SearchJobStatus.PENDING); // Uses your Enum
        
        return searchJobRepo.save(job);
    }

    public String getJobStatus(Long jobId) throws Exception {
        SearchJob job = searchJobRepo.findById(jobId)
            .orElseThrow(() -> new Exception("Job not found"));
            
        return job.getStatus().name();
    }

    public void updateJobStatus(Long jobId, SearchJobStatus status) throws Exception {
        SearchJob job = searchJobRepo.findById(jobId)
            .orElseThrow(() -> new Exception("Error: Job Not Found"));
            
        job.setStatus(status);
        searchJobRepo.save(job);
    }

    public List<SearchJobDto> getHistory(String username) {
        return searchJobRepo.findByRequestedByUsernameOrderByIdDesc(username).stream()
            .map(job -> new SearchJobDto(
                job.getId(),
                new CompanyDto(job.getCompany().getId(), job.getCompany().getName(), job.getCompany().getDomain()),
                new UserDto(job.getRequestedBy().getId(), job.getRequestedBy().getUsername()),
                job.getStatus().name()
            ))
            .toList();
    }
}
