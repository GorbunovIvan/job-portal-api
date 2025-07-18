package com.example.api;

import com.example.model.Application;
import com.example.model.Job;
import com.example.model.enums.ApplicationStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApplicationResponseDTOTest {

    @Test
    void shouldReturnApplicationResponseDTOWhenFromApplication() {

        var expectedId = 1L;
        var expectedJobTitle = "Software Engineer";
        var expectedAppliedAt = Instant.now();
        var expectedStatus = ApplicationStatus.ACCEPTED;

        Job jobMock = mock(Job.class);
        when(jobMock.getTitle()).thenReturn(expectedJobTitle);

        Application applicationMock = mock(Application.class);
        when(applicationMock.getId()).thenReturn(expectedId);
        when(applicationMock.getJob()).thenReturn(jobMock);
        when(applicationMock.getAppliedAt()).thenReturn(expectedAppliedAt);
        when(applicationMock.getStatus()).thenReturn(expectedStatus);

        ApplicationResponseDTO dto = ApplicationResponseDTO.fromApplication(applicationMock);

        assertNotNull(dto);
        assertEquals(expectedId, dto.getId());
        assertEquals(expectedJobTitle, dto.getJobTitle());
        assertEquals(expectedAppliedAt, dto.getAppliedAt());
        assertEquals(expectedStatus, dto.getStatus());
    }
}