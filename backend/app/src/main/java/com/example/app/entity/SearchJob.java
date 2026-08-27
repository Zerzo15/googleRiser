package com.example.app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    // Keep the existing PostgreSQL schema compatible: status is stored as a
    // small ordinal with a 0..3 check constraint in the current database.
    @Enumerated(EnumType.ORDINAL)
    @JdbcTypeCode(SqlTypes.SMALLINT)
    private SearchJobStatus status;

    public Long getId() { return id; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public User getRequestedBy() { return requestedBy; }
    public void setRequestedBy(User requestedBy) { this.requestedBy = requestedBy; }
    public SearchJobStatus getStatus() { return status; }
    public void setStatus(SearchJobStatus status) { this.status = status; }
}
