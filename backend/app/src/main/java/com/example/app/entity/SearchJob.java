package com.example.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "search_jobs")
public class SearchJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requestedBy; // Links directly to your existing User.java

    @Enumerated(EnumType.STRING)
    private SearchJobStatus status;

    public Long getId() { return id; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public User getRequestedBy() { return requestedBy; }
    public void setRequestedBy(User requestedBy) { this.requestedBy = requestedBy; }
    public SearchJobStatus getStatus() { return status; }
    public void setStatus(SearchJobStatus status) { this.status = status; }
}
