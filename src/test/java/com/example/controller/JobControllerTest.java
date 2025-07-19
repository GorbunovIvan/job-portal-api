package com.example.controller;

import com.example.api.JobRequestDTO;
import com.example.api.JobResponseDTO;
import com.example.model.enums.JobType;
import com.example.model.user.Role;
import com.example.model.user.User;
import com.example.security.JwtUtil;
import com.example.service.JobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService jobService;
    @MockitoBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private JobRequestDTO jobRequestDTO;
    private JobResponseDTO jobResponseDTO;

    @Autowired
    private JwtUtil jwtUtil;

    private User userEmployer;

    private String tokenForEmployer;

    @PostConstruct
    private void init() {

        jobRequestDTO = new JobRequestDTO();
        jobRequestDTO.setTitle("Backend Developer");
        jobRequestDTO.setDescription("Java backend dev");
        jobRequestDTO.setLocation("Remote");
        jobRequestDTO.setSalaryMin(60000);
        jobRequestDTO.setSalaryMax(80000);
        jobRequestDTO.setJobType(JobType.FULL_TIME);

        jobResponseDTO = new JobResponseDTO();
        jobResponseDTO.setId(1L);
        jobResponseDTO.setTitle("Backend Developer");
        jobResponseDTO.setLocation("Remote");
        jobResponseDTO.setJobType(JobType.FULL_TIME);

        var roleEmployer = new Role(2, "EMPLOYER");
        userEmployer = new User(2L, "employer", "employer-password", "full-name-empl", null, null, Set.of(roleEmployer));

        tokenForEmployer = jwtUtil.generateToken(userEmployer);
    }

    @BeforeEach
    void setUp() {
        when(userDetailsService.loadUserByUsername(userEmployer.getUsername())).thenReturn(userEmployer);
    }

    @Test
    void shouldReturnForbiddenWhenFindAllNoToken() throws Exception {

        mockMvc.perform(get("/api/v1/jobs"))
                .andExpect(status().isForbidden());

        verify(jobService, never()).findAll();
        verify(jobService, never()).findAll(any());
    }

    @Test
    void shouldReturnUnauthorizedWhenFindAllWrongToken() throws Exception {

        var wrongToken = userEmployer + "1";

        mockMvc.perform(get("/api/v1/jobs")
                        .header("Authorization", "Bearer " + wrongToken))
                .andExpect(status().isUnauthorized());

        verify(jobService, never()).findAll();
        verify(jobService, never()).findAll(any());
    }

    @Test
    void shouldReturnNotFoundWhenJobNotFoundById() throws Exception {

        when(jobService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/jobs/1")
                        .header("Authorization", "Bearer " + tokenForEmployer))
                .andExpect(status().isNotFound());

        verify(jobService).findById(1L);
    }

    @Test
    void shouldReturnAllJobsWhenKeywordIsNull() throws Exception {

        when(jobService.findAll(null)).thenReturn(List.of(jobResponseDTO));

        mockMvc.perform(get("/api/v1/jobs")
                        .header("Authorization", "Bearer " + tokenForEmployer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(jobService).findAll(null);
    }

    @Test
    void shouldReturnJobsByKeyword() throws Exception {

        when(jobService.findAll("backend")).thenReturn(List.of(jobResponseDTO));

        mockMvc.perform(get("/api/v1/jobs?keyword=backend")
                        .header("Authorization", "Bearer " + tokenForEmployer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Backend Developer"));

        verify(jobService).findAll("backend");
    }

    @Test
    void shouldReturnJobWhenFindById() throws Exception {

        var jobId = 3L;

        when(jobService.findById(jobId)).thenReturn(jobResponseDTO);

        mockMvc.perform(get("/api/v1/jobs/{jobId}", jobId)
                        .header("Authorization", "Bearer " + tokenForEmployer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Backend Developer"));

        verify(jobService).findById(jobId);
    }

    @Test
    void shouldReturnJobsByCurrentEmployer() throws Exception {

        var employerId = userEmployer.getId();
        
        when(jobService.findAllByEmployer(employerId)).thenReturn(List.of(jobResponseDTO));

        mockMvc.perform(get("/api/v1/jobs/my")
                        .header("Authorization", "Bearer " + tokenForEmployer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Backend Developer"));

        verify(jobService).findAllByEmployer(employerId);
    }

    @Test
    void shouldReturnJobsByEmployer() throws Exception {

        var employerId = userEmployer.getId();

        when(jobService.findAllByEmployer(employerId)).thenReturn(List.of(jobResponseDTO));

        mockMvc.perform(get("/api/v1/jobs/employer/{employerId}", employerId)
                        .header("Authorization", "Bearer " + tokenForEmployer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Backend Developer"));

        verify(jobService).findAllByEmployer(employerId);
    }

    @Test
    void shouldCreateJobAndReturnIt() throws Exception {

        var employerId = userEmployer.getId();

        when(jobService.create(jobRequestDTO, employerId)).thenReturn(jobResponseDTO);

        mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + tokenForEmployer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Backend Developer"));

        verify(jobService).create(jobRequestDTO, employerId);
    }

    @Test
    void shouldUpdateJobAndReturnUpdated() throws Exception {

        var employerId = userEmployer.getId();
        var jobId = 3L;

        when(jobService.update(jobId, jobRequestDTO, employerId)).thenReturn(jobResponseDTO);

        mockMvc.perform(put("/api/v1/jobs/{jobId}", jobId)
                        .header("Authorization", "Bearer " + tokenForEmployer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Backend Developer"));

        verify(jobService).update(jobId, jobRequestDTO, employerId);
    }

    @Test
    void shouldDeleteJobAndReturnAccepted() throws Exception {

        var employerId = userEmployer.getId();
        var jobId = 3L;

        doNothing().when(jobService).delete(any(), any());

        mockMvc.perform(delete("/api/v1/jobs/{jobId}", jobId)
                    .header("Authorization", "Bearer " + tokenForEmployer))
                .andExpect(status().isAccepted());

        verify(jobService).delete(jobId, employerId);
    }
}
