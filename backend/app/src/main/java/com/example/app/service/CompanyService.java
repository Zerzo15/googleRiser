package com.example.app.service;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
    private static final int MAX_COMPANY_NAME_LENGTH = 200;
    private static final int MAX_DOMAIN_LENGTH = 253;

    private final CompanyProfileRepo companyProfileRepo;
    private final CompanyRepo companyRepo;
    private final DataSourceRepo dataSourceRepo;

    public CompanyService(CompanyRepo companyRepo, CompanyProfileRepo companyProfileRepo, DataSourceRepo dataSourceRepo) {
        this.companyRepo = companyRepo;
        this.companyProfileRepo = companyProfileRepo;
        this.dataSourceRepo = dataSourceRepo;
    }

    public CompanyDto registerCompany(Company company, String username) throws Exception{

        String safeName = company.getName() == null ? null : company.getName().trim();
        String safeDomain = normalizeDomain(company.getDomain());

        if (safeName == null || safeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter Company Name");
        }
        if (safeName.length() > MAX_COMPANY_NAME_LENGTH) {
            throw new IllegalArgumentException("Company name is too long");
        }
        if (safeDomain != null && !isValidDomain(safeDomain)) {
            throw new IllegalArgumentException("Invalid company website");
        }

        Optional<Company> existingCompany = companyRepo.findByNameIgnoreCase(safeName);
        if (existingCompany.isEmpty() && safeDomain != null && !safeDomain.isEmpty()) {
            existingCompany = companyRepo.findByDomainIgnoreCase(safeDomain);
        }

        if (existingCompany.isPresent()) {
            Company companyInDatabase = existingCompany.get();
            String existingDomain = normalizeDomain(companyInDatabase.getDomain());
            if (safeDomain == null || existingDomain == null || safeDomain.equals(existingDomain)) {
                if (existingDomain != null && !existingDomain.equals(companyInDatabase.getDomain())) {
                    companyInDatabase.setDomain(existingDomain);
                    companyRepo.save(companyInDatabase);
                }
                return new CompanyDto(
                    companyInDatabase.getId(),
                    companyInDatabase.getName(),
                    existingDomain
                );
            }
            throw new IllegalArgumentException("Company Already Registered");
        }

        company.setName(safeName);
        company.setDomain(safeDomain);
        Company savedCompany = companyRepo.save(company);

        return new CompanyDto(
            savedCompany.getId(),
            safeName,
            safeDomain
        );
    }

    /**
     * Stores one canonical host regardless of whether the user entered a host
     * or pasted a complete URL such as https://vng.com.vn/.
     */
    static String normalizeDomain(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String candidate = value.trim();
        try {
            URI parsed = URI.create(candidate.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$")
                    ? candidate
                    : "https://" + candidate);
            if (parsed.getHost() != null) {
                return parsed.getHost().toLowerCase(Locale.ROOT).replaceFirst("^www\\.", "");
            }
        } catch (IllegalArgumentException ignored) {
            // Fall back to a conservative string cleanup below.
        }

        return candidate
                .replaceFirst("^[a-zA-Z][a-zA-Z0-9+.-]*://", "")
                .split("[/?#]", 2)[0]
                .toLowerCase(Locale.ROOT)
                .replaceFirst("^www\\.", "");
    }

    private static boolean isValidDomain(String domain) {
        if (domain.length() > MAX_DOMAIN_LENGTH) {
            return false;
        }
        String label = "[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?";
        return domain.matches(label + "(?:\\." + label + ")+" );
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
