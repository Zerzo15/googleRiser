package com.example.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "data_sources")
public class DataSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private CompanyProfile companyProfile;

    private String url;
    private String platformName;
    
    @Column(columnDefinition = "TEXT")
    private String rawData;

    public Long getId() { return id; }
    public CompanyProfile getCompanyProfile() { return companyProfile; }
    public void setCompanyProfile(CompanyProfile companyProfile) { this.companyProfile = companyProfile; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getPlatformName() { return platformName; }
    public void setPlatformName(String platformName) { this.platformName = platformName; }
    public String getRawData() { return rawData; }
    public void setRawData(String rawData) { this.rawData = rawData; }
}