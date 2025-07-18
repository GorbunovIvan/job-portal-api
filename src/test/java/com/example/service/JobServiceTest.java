package com.example.service;

import com.example.api.JobRequestDTO;
import com.example.api.JobResponseDTO;
import com.example.model.Employer;
import com.example.model.Job;
import com.example.model.enums.JobType;
import com.example.repository.EmployerRepository;
import com.example.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class JobServiceTest {

    @Autowired
    private JobService jobService;

    @MockitoBean
    private JobRepository jobRepository;

    @MockitoBean
    private EmployerRepository employerRepository;

    private Employer employerMock;
    private Job jobMock;

    @BeforeEach
    void setUp() {
        
        employerMock = new Employer();
        employerMock.setId(1L);
        employerMock.setCompanyName("Test Company");

        jobMock = new Job();
        jobMock.setId(100L);
        jobMock.setTitle("Java Developer");
        jobMock.setDescription("Backend role");
        jobMock.setLocation("Remote");
        jobMock.setSalaryMin(50000);
        jobMock.setSalaryMax(90000);
        jobMock.setJobType(JobType.FULL_TIME);
        employerMock.addJob(jobMock);
    }

    @Test
    void shouldReturnJobWhenFindByIdFound() {

        when(jobRepository.findById(100L)).thenReturn(Optional.of(jobMock));

        var result = jobService.findById(100L);

        assertNotNull(result);
        assertEquals("Java Developer", result.getTitle());
    }

    @Test
    void shouldReturnNullWhenFindByIdFound() {
        when(jobRepository.findById(200L)).thenReturn(Optional.empty());
        var result = jobService.findById(200L);
        assertNull(result);
    }

    @Test
    void shouldReturnJobsWhenFindAllWithoutKeyword() {

        when(jobRepository.findAll()).thenReturn(List.of(jobMock));

        var result = jobService.findAll();

        assertEquals(1, result.size());
        assertEquals("Java Developer", result.getFirst().getTitle());
    }

    @Test
    void shouldReturnJobsWhenFindAllWithKeyword() {
        when(jobRepository.findByTitleContainingIgnoreCase("Java")).thenReturn(List.of(jobMock));
        var result = jobService.findAll("Java");
        assertEquals(1, result.size());
        assertEquals(JobResponseDTO.fromJob(jobMock), result.getFirst());
    }

    @Test
    void shouldReturnJobsWhenFindAllByEmployer() {
        when(jobRepository.findByEmployerId(1L)).thenReturn(List.of(jobMock));
        var result = jobService.findAllByEmployer(1L);
        assertEquals(1, result.size());
        assertEquals(JobResponseDTO.fromJob(jobMock), result.getFirst());
    }

    @Test
    void shouldCreateAndReturnJobWhenCreate() {

        var dto = new JobRequestDTO();
        dto.setTitle("Java Developer");
        dto.setDescription("Job Desc");
        dto.setLocation("Remote");
        dto.setSalaryMin(50000);
        dto.setSalaryMax(90000);
        dto.setJobType(JobType.FULL_TIME);

        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employerMock));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> {
            Job job = invocation.getArgument(0);
            job.setId(999L);
            return job;
        });

        var result = jobService.create(dto, 1L);

        assertNotNull(result);
        assertEquals("Java Developer", result.getTitle());
        assertEquals(999L, result.getId());
    }

    @Test
    void shouldThrowEmployerNotFoundWhenCreate() {
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.empty());
        var dto = new JobRequestDTO();
        assertThrows(RuntimeException.class, () -> jobService.create(dto, 1L));
    }

    @Test
    void shouldUpdateAndReturnJobWhenUpdate() {

        var dto = new JobRequestDTO();
        dto.setTitle("Updated");
        dto.setDescription("Updated Desc");
        dto.setLocation("Updated Loc");
        dto.setSalaryMin(60000);
        dto.setSalaryMax(95000);
        dto.setJobType(JobType.CONTRACT);

        when(jobRepository.findById(100L)).thenReturn(Optional.of(jobMock));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = jobService.update(100L, dto, 1L);

        assertEquals("Updated", result.getTitle());
        assertEquals(JobType.CONTRACT, result.getJobType());
    }

    @Test
    void shouldThrowJobNotFoundWhenUpdate() {
        var dto = new JobRequestDTO();
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> jobService.update(999L, dto, 1L));
    }

    @Test
    void shouldThrowWrongEmployerWhenUpdate() {

        employerMock.setId(999L);  // Different employer ID
        employerMock.addJob(jobMock);

        when(jobRepository.findById(100L)).thenReturn(Optional.of(jobMock));

        var dto = new JobRequestDTO();

        assertThrows(RuntimeException.class, () -> jobService.update(100L, dto, 1L));
    }

    @Test
    void shouldReturnNothingWhenDelete() {

        when(jobRepository.findById(100L)).thenReturn(Optional.of(jobMock));

        employerMock.setId(1L);  // Match employer
        employerMock.addJob(jobMock);

        jobService.delete(100L, 1L);

        verify(jobRepository).delete(jobMock);
    }

    @Test
    void shouldReturnNothingWhenDeleteWithJobNotFound() {

        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        // Should not throw
        jobService.delete(999L, 1L);

        verify(jobRepository, never()).delete(any());
    }

    @Test
    void shouldThrowWrongEmployerWhenDelete() {

        employerMock.setId(2L);  // Different
        employerMock.addJob(jobMock);

        when(jobRepository.findById(100L)).thenReturn(Optional.of(jobMock));

        assertThrows(RuntimeException.class, () -> jobService.delete(100L, 1L));
    }
}
