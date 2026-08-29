package com.example.app.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.CompanyDto;
import com.example.app.entity.Company;
import com.example.app.entity.SearchJob;
import com.example.app.entity.CompanyProfile;
import com.example.app.service.AutomationService;
import com.example.app.service.CompanyService;
import com.example.app.service.SearchJobService;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final SearchJobService searchJobService;
    private final AutomationService automationService;

    public CompanyController(CompanyService companyService, SearchJobService searchJobService, AutomationService automationService) {
        this.companyService = companyService;
        this.searchJobService = searchJobService;
        this.automationService = automationService;
    }

    @PostMapping("/research")
    public ResponseEntity<?> startResearch(@RequestBody Company company, Principal principal) throws Exception {
        CompanyDto savedCompany = companyService.registerCompany(company, principal.getName());
        SearchJob job = searchJobService.initializeJob(savedCompany.id(), principal.getName());
        
        automationService.processJob(job.getId(), savedCompany.id());
        
        return ResponseEntity.ok(Map.of(
            "jobId", job.getId(),
            "companyId", savedCompany.id(),
            "status", job.getStatus().name()
        ));
    }

    @GetMapping("/jobs/{jobId}/status")
    public ResponseEntity<?> getJobStatus(@PathVariable Long jobId, Principal principal) throws Exception {
        String status = searchJobService.getJobStatus(jobId, principal.getName());
        return ResponseEntity.ok(Map.of("status", status));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(Principal principal) {
        return ResponseEntity.ok(searchJobService.getHistory(principal.getName()));
    }

    @GetMapping("/{companyId}/profile")
    public ResponseEntity<?> getCompanyProfile(@PathVariable Long companyId, Principal principal) throws Exception {
        searchJobService.assertCompanyAccess(companyId, principal.getName());
        CompanyProfile profile = companyService.getProfile(companyId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{companyId}/sources")
    public ResponseEntity<?> getCompanySources(@PathVariable Long companyId, Principal principal) throws Exception {
        searchJobService.assertCompanyAccess(companyId, principal.getName());
        return ResponseEntity.ok(companyService.getSources(companyId));
    }
}
