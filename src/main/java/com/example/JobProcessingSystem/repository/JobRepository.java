package com.example.JobProcessingSystem.repository;

import com.example.JobProcessingSystem.Entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, String> {
}
