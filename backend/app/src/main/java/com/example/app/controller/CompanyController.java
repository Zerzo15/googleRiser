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
import com.example.app.entity.CompanyProfile;
import com.example.app.entity.SearchJob;
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

    // Step 1: Frontend sends company details. We save it, create a job, and start the background task.
    @PostMapping("/research")
    public ResponseEntity<?> startResearch(@RequestBody Company company, Principal principal) {
        try {
            // 1. Register the company
            CompanyDto savedCompany = companyService.registerCompany(company, principal.getName());
            
            // 2. Initialize the PENDING job
            SearchJob job = searchJobService.initializeJob(savedCompany.id(), principal.getName());
            
            // 3. Fire the async background scraping task (this runs on a separate thread)
            automationService.processJob(job.getId(), savedCompany.id());
            
            // 4. Return the Job ID to the frontend so it can start polling
            return ResponseEntity.ok(Map.of(
                "jobId", job.getId(),
                "companyId", savedCompany.id(),
                "status", job.getStatus().name()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Step 2: Frontend polls this endpoint every few seconds to check if status is COMPLETE
    @GetMapping("/jobs/{jobId}/status")
    public ResponseEntity<?> getJobStatus(@PathVariable Long jobId) {
        try {
            String status = searchJobService.getJobStatus(jobId);
            return ResponseEntity.ok(Map.of("status", status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(Principal principal) {
        return ResponseEntity.ok(searchJobService.getHistory(principal.getName()));
    }

    // Step 3: Once the job is COMPLETE, frontend fetches the finalized report
    @GetMapping("/{companyId}/profile")
    public ResponseEntity<?> getCompanyProfile(@PathVariable Long companyId) {
        try {
            CompanyProfile profile = companyService.getProfile(companyId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{companyId}/sources")
    public ResponseEntity<?> getCompanySources(@PathVariable Long companyId) {
        try {
            return ResponseEntity.ok(companyService.getSources(companyId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
