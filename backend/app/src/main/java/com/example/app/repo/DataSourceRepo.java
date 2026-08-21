package com.example.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.entity.DataSource;

public interface DataSourceRepo extends JpaRepository<DataSource, Long>{


} 