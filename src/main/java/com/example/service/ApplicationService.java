package com.example.service;

import com.example.api.ApplicationRequestDTO;
import com.example.api.ApplicationResponseDTO;
import com.example.model.Application;
import com.example.model.enums.ApplicationStatus;
import com.example.repository.ApplicantRepository;
import com.example.repository.ApplicationRepository;
import com.example.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final ApplicantRepository applicantRepository;
    private final JobRepository jobRepository;

    public List<ApplicationResponseDTO> findAllByApplicant(Long userId) {

        log.info("Finding applications by applicant id: {}", userId);

        var applicant = applicantRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        return applicationRepository.findByApplicantId(applicant.getId())
                .stream()
                .map(ApplicationResponseDTO::fromApplication)
                .toList();
    }

    @Transactional
    public void applyApplicantToJob(Long userId, ApplicationRequestDTO dto) {

        log.info("Creating application by applicant id: {}", userId);

        var applicant = applicantRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        var job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        var applicationOpt = applicationRepository.findByApplicantAndJob(applicant, job);
        if (applicationOpt.isPresent()) {
            throw new RuntimeException("You already applied to this job.");
        }

        var application = new Application();
        application.setCoverLetter(dto.getCoverLetter());
        application.setResumeLink(dto.getResumeLink());
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(Instant.now());

        applicant.addApplication(application);
        job.addApplication(application);

        applicationRepository.save(application);
    }

    @Transactional
    public void deleteApplication(Long userId, Long jobId) {

        log.info("Deleting application for a job id: {}, employer id: {}", jobId, userId);

        var applicant = applicantRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        var job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        var applicationOpt = applicationRepository.findByApplicantAndJob(applicant, job);
        applicationOpt.ifPresent(applicationRepository::delete);
    }
}
