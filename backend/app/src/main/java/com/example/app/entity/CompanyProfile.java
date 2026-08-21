package com.example.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_profiles")
public class CompanyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private String sector;
    private String scale;
    
    @Column(columnDefinition = "TEXT")
    private String products;
    private String market;
    private LocalDateTime lastUpdated;

    public Long getId() { return id; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    public String getScale() { return scale; }
    public void setScale(String scale) { this.scale = scale; }
    public String getProducts() { return products; }
    public void setProducts(String products) { this.products = products; }
    public String getMarket() { return market; }
    public void setMarket(String market) { this.market = market; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}