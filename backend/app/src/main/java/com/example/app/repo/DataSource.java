package com.example.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.entity.User;

public interface DataSource extends JpaRepository<User, Long>{


} 