package com.example.app.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.entity.DataSource;

public interface DataSourceRepo extends JpaRepository<DataSource, Long>{

    List<DataSource> findByCompanyProfileId(Long profileId);
}
