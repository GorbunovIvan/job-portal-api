package com.example.service;

import com.example.api.ApplicationRequestDTO;
import com.example.api.ApplicationResponseDTO;
import com.example.model.Applicant;
import com.example.model.Application;
import com.example.model.Job;
import com.example.model.enums.ApplicationStatus;
import com.example.repository.ApplicantRepository;
import com.example.repository.ApplicationRepository;
import com.example.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ApplicationServiceTest {
    
    @Autowired
    private ApplicationService applicationService;

    @MockitoBean
    private ApplicationRepository applicationRepository;
    @MockitoBean
    private ApplicantRepository applicantRepository;
    @MockitoBean
    private JobRepository jobRepository;

    private Applicant applicantMock;
    private Job jobMock;
    private Application applicationMock;

    @BeforeEach
    void setUp() {

        applicantMock = new Applicant();
        applicantMock.setId(1L);

        jobMock = new Job();
        jobMock.setId(10L);
        jobMock.setTitle("Java Dev");

        applicationMock = new Application();
        applicationMock.setId(100L);
        applicationMock.setCoverLetter("Sample cover");
        applicationMock.setResumeLink("http://resume.com");
        applicationMock.setStatus(ApplicationStatus.PENDING);
        applicationMock.setAppliedAt(Instant.now());

        applicantMock.addApplication(applicationMock);
        jobMock.addApplication(applicationMock);
    }

    @Test
    void shouldReturnApplicationsWhenFindingByApplicantId() {

        when(applicantRepository.findByUserId(1L)).thenReturn(Optional.of(applicantMock));
        when(applicationRepository.findByApplicantId(1L)).thenReturn(List.of(applicationMock));

        var result = applicationService.findAllByApplicant(1L);

        assertEquals(1, result.size());
        assertEquals(ApplicationResponseDTO.fromApplication(applicationMock), result.getFirst());
    }

    @Test
    void shouldThrowApplicantNotFoundWhenFindAllByApplicant() {
        when(applicantRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> applicationService.findAllByApplicant(1L));
    }

    @Test
    void shouldCreateAndReturnApplicationWhenApplyApplicantToJob() {

        var dto = new ApplicationRequestDTO();
        dto.setJobId(10L);
        dto.setCoverLetter("Nice job");
        dto.setResumeLink("http://resume");

        when(applicantRepository.findByUserId(1L)).thenReturn(Optional.of(applicantMock));
        when(jobRepository.findById(10L)).thenReturn(Optional.of(jobMock));
        when(applicationRepository.findByApplicantAndJob(applicantMock, jobMock)).thenReturn(Optional.empty());

        applicationService.applyApplicantToJob(1L, dto);

        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void shouldThrowApplicantNotFoundWhenApplyApplicantToJob() {

        var dto = new ApplicationRequestDTO();
        dto.setJobId(10L);

        when(applicantRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> applicationService.applyApplicantToJob(1L, dto));
    }

    @Test
    void shouldThrowJobNotFoundWhenApplyApplicantToJob() {

        var dto = new ApplicationRequestDTO();
        dto.setJobId(10L);

        when(applicantRepository.findByUserId(1L)).thenReturn(Optional.of(applicantMock));
        when(jobRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> applicationService.applyApplicantToJob(1L, dto));
    }

    @Test
    void shouldThrowApplicationAlreadyExistsWhenApplyApplicantToJob() {

        var dto = new ApplicationRequestDTO();
        dto.setJobId(10L);

        when(applicantRepository.findByUserId(1L)).thenReturn(Optional.of(applicantMock));
        when(jobRepository.findById(10L)).thenReturn(Optional.of(jobMock));
        when(applicationRepository.findByApplicantAndJob(applicantMock, jobMock)).thenReturn(Optional.of(applicationMock));

        assertThrows(RuntimeException.class, () -> applicationService.applyApplicantToJob(1L, dto));
    }

    @Test
    void shouldReturnNothingWhenDelete() {

        when(applicantRepository.findByUserId(1L)).thenReturn(Optional.of(applicantMock));
        when(jobRepository.findById(10L)).thenReturn(Optional.of(jobMock));
        when(applicationRepository.findByApplicantAndJob(applicantMock, jobMock)).thenReturn(Optional.of(applicationMock));

        applicationService.deleteApplication(1L, 10L);

        verify(applicationRepository).delete(applicationMock);
    }

    @Test
    void shouldReturnNothingWhenDeleteButExists() {

        when(applicantRepository.findByUserId(1L)).thenReturn(Optional.of(applicantMock));
        when(jobRepository.findById(10L)).thenReturn(Optional.of(jobMock));
        when(applicationRepository.findByApplicantAndJob(applicantMock, jobMock)).thenReturn(Optional.empty());

        applicationService.deleteApplication(1L, 10L);

        verify(applicationRepository, never()).delete(any());
    }

    @Test
    void shouldThrowApplicantNotFoundWhenDeleteApplication() {
        when(applicantRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> applicationService.deleteApplication(1L, 10L));
    }

    @Test
    void shouldThrowJobNotFoundWhenDeleteApplication() {
        when(applicantRepository.findByUserId(1L)).thenReturn(Optional.of(applicantMock));
        when(jobRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> applicationService.deleteApplication(1L, 10L));
    }
}
