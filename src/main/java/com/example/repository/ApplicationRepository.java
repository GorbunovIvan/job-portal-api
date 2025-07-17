package com.example.repository;

import com.example.model.Applicant;
import com.example.model.Application;
import com.example.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobId(Long jobId);
    List<Application> findByApplicantId(Long applicantId);
    Optional<Application> findByApplicantAndJob(Applicant applicant, Job job);
}
