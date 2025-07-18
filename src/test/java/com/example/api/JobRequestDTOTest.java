package com.example.api;

import com.example.model.Job;
import com.example.model.enums.JobType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobRequestDTOTest {

    @Test
    void shouldReturnJobWhenToJob() {

        var dto = new JobRequestDTO();
        dto.setTitle("Backend Developer");
        dto.setDescription("Build and maintain backend systems.");
        dto.setLocation("Remote");
        dto.setSalaryMin(60000);
        dto.setSalaryMax(90000);
        dto.setJobType(JobType.FULL_TIME);

        Job job = JobRequestDTO.toJob(dto);

        assertNotNull(job);
        assertEquals(dto.getTitle(), job.getTitle());
        assertEquals(dto.getDescription(), job.getDescription());
        assertEquals(dto.getLocation(), job.getLocation());
        assertEquals(dto.getSalaryMin(), job.getSalaryMin());
        assertEquals(dto.getSalaryMax(), job.getSalaryMax());
        assertEquals(dto.getJobType(), job.getJobType());
    }
}