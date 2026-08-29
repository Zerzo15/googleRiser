package com.example.app.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.entity.Company;

public interface CompanyRepo extends JpaRepository<Company, Long>{

    boolean existsByName(String name);

    boolean existsByDomain(String domain);

    Optional<Company> findByNameIgnoreCase(String name);

    Optional<Company> findByDomainIgnoreCase(String domain);

}
