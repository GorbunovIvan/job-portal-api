package com.example.service;

import com.example.api.JobRequestDTO;
import com.example.api.JobResponseDTO;
import com.example.model.Job;
import com.example.repository.EmployerRepository;
import com.example.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;

    public JobResponseDTO findById(Long id) {
        log.info("Finding job by id: {}", id);
        return jobRepository.findById(id)
                .map(JobResponseDTO::fromJob)
                .orElse(null);
    }

    public List<JobResponseDTO> findAll() {
        return findAll(null);
    }

    public List<JobResponseDTO> findAll(String keyword) {

        List<Job> jobs;

        if (keyword == null) {
            log.info("Finding all jobs");
            jobs = jobRepository.findAll();
        } else {
            log.info("Finding jobs by keyword: {}", keyword);
            jobs = jobRepository.findByTitleContainingIgnoreCase(keyword);
        }

        return jobs.stream()
                .map(JobResponseDTO::fromJob)
                .toList();
    }

    public List<JobResponseDTO> findAllByEmployer(Long userId) {
        log.info("Finding jobs by employer id: {}", userId);
        return jobRepository.findByEmployerId(userId).stream()
                .map(JobResponseDTO::fromJob)
                .toList();
    }

    @Transactional
    public JobResponseDTO create(JobRequestDTO jobDto, Long userId) {

        log.info("Creating job, employer id: {}", userId);

        var employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        var job = JobRequestDTO.toJob(jobDto);
        employer.addJob(job);

        Job jobSaved = jobRepository.save(job);
        return JobResponseDTO.fromJob(jobSaved);
    }

    @Transactional
    public JobResponseDTO update(Long jobId, JobRequestDTO jobDto, Long userId) {

        log.info("Updating job with id: {}, employer id: {}", jobId, userId);

        var jobExistingOpt = jobRepository.findById(jobId);
        if (jobExistingOpt.isEmpty()) {
            throw new RuntimeException("Job not found");
        }
        var jobExisting = jobExistingOpt.get();
        if (!Objects.equals(userId, jobExisting.getEmployer().getId())) {
            throw new RuntimeException("You are not the employer of this job");
        }

        jobExisting.setTitle(jobDto.getTitle());
        jobExisting.setDescription(jobDto.getDescription());
        jobExisting.setLocation(jobDto.getLocation());
        jobExisting.setSalaryMin(jobDto.getSalaryMin());
        jobExisting.setSalaryMax(jobDto.getSalaryMax());
        jobExisting.setJobType(jobDto.getJobType());

        var jobUpdated = jobRepository.save(jobExisting);
        return JobResponseDTO.fromJob(jobUpdated);
    }

    @Transactional
    public void delete(Long jobId, Long userId) {

        log.info("Deleting job with id: {}, employer id: {}", jobId, userId);

        var jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            return;
        }
        var job = jobOpt.get();

        if (!Objects.equals(userId, job.getEmployer().getId())) {
            throw new RuntimeException("You are not the employer of this job");
        }

        jobRepository.delete(job);
    }
}
