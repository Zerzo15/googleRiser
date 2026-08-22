package com.example.app.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.entity.SearchJob;

public interface SearchJobRepo extends JpaRepository<SearchJob, Long>{

    List<SearchJob> findByRequestedByUsernameOrderByIdDesc(String username);
}
