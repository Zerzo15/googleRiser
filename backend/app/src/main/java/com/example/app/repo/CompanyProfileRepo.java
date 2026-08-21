package com.example.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.entity.User;

public interface CompanyProfileRepo extends JpaRepository<User, Long>{


} 