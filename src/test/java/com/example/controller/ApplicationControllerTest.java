package com.example.controller;

import com.example.api.ApplicationRequestDTO;
import com.example.api.ApplicationResponseDTO;
import com.example.model.enums.ApplicationStatus;
import com.example.model.user.Role;
import com.example.model.user.User;
import com.example.security.JwtUtil;
import com.example.service.ApplicationService;
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

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApplicationService applicationService;
    @MockitoBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ApplicationRequestDTO applicationRequestDTO;
    private ApplicationResponseDTO applicationResponseDTO;

    @Autowired
    private JwtUtil jwtUtil;

    private User userApplicant;
    private User userEmployer;

    private String tokenForApplicant;
    private String tokenForEmployer;

    @PostConstruct
    private void init() {

        applicationRequestDTO = new ApplicationRequestDTO();
        applicationRequestDTO.setJobId(10L);
        applicationRequestDTO.setCoverLetter("Excited to apply");
        applicationRequestDTO.setResumeLink("https://resume.link");

        applicationResponseDTO = new ApplicationResponseDTO();
        applicationResponseDTO.setId(1L);
        applicationResponseDTO.setJobTitle("Java Developer");
        applicationResponseDTO.setAppliedAt(Instant.now());
        applicationResponseDTO.setStatus(ApplicationStatus.PENDING);

        var roleApplicant = new Role(1, "APPLICANT");
        var roleEmployer = new Role(2, "EMPLOYER");

        userApplicant = new User(1L, "applicant", "applicant-password", "full-name-appl", null, null, Set.of(roleApplicant));
        userEmployer = new User(2L, "employer", "employer-password", "full-name-empl", null, null, Set.of(roleEmployer));

        tokenForApplicant = jwtUtil.generateToken(userApplicant);
        tokenForEmployer = jwtUtil.generateToken(userEmployer);
    }

    @BeforeEach
    void setUp() {
        when(userDetailsService.loadUserByUsername(userApplicant.getUsername())).thenReturn(userApplicant);
        when(userDetailsService.loadUserByUsername(userEmployer.getUsername())).thenReturn(userEmployer);
    }

    @Test
    void shouldReturnForbiddenWhenGetAllNoToken() throws Exception {

        mockMvc.perform(get("/api/v1/applications"))
                .andExpect(status().isForbidden());

        verify(applicationService, never()).findAllByApplicant(any());
    }

    @Test
    void shouldReturnUnauthorizedWhenGetAllWrongToken() throws Exception {

        var wrongToken = userApplicant + "1";

        mockMvc.perform(get("/api/v1/applications")
                        .header("Authorization", "Bearer " + wrongToken))
                .andExpect(status().isUnauthorized());

        verify(applicationService, never()).findAllByApplicant(any());
    }

    @Test
    void shouldReturnApplicationsWhenGetAll() throws Exception {

        var userId = userApplicant.getId();

        List<ApplicationResponseDTO> mockList = List.of(applicationResponseDTO);
        when(applicationService.findAllByApplicant(userId)).thenReturn(mockList);

        mockMvc.perform(get("/api/v1/applications")
                    .header("Authorization", "Bearer " + tokenForApplicant))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].jobTitle").value("Java Developer"));

        verify(applicationService).findAllByApplicant(userId);
    }

    @Test
    void shouldReturnAcceptedWhenCreateApplication() throws Exception {

        var userId = userApplicant.getId();

        doNothing().when(applicationService).applyApplicantToJob(userId, applicationRequestDTO);

        mockMvc.perform(post("/api/v1/applications")
                        .header("Authorization", "Bearer " + tokenForApplicant)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applicationRequestDTO)))
                .andExpect(status().isAccepted());

        verify(applicationService).applyApplicantToJob(userId, applicationRequestDTO);
    }

    @Test
    void shouldReturnAcceptedWhenDeleteApplication() throws Exception {

        var userId = userApplicant.getId();
        var jobId = 10L;

        doNothing().when(applicationService).deleteApplication(userId, jobId);

        mockMvc.perform(delete("/api/v1/applications/{jobId}", jobId)
                    .header("Authorization", "Bearer " + tokenForApplicant))
                .andExpect(status().isAccepted());

        verify(applicationService).deleteApplication(userId, jobId);
    }
}
