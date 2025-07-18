package com.example.api;

import com.example.model.Employer;
import com.example.model.Job;
import com.example.model.enums.JobType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JobResponseDTOTest {

    @Test
    void shouldReturnJobResponseDTOWhenFromJob() {

        var expectedId = 10L;
        var expectedTitle = "DevOps Engineer";
        var expectedDescription = "Maintain CI/CD pipelines.";
        var expectedLocation = "Berlin";
        int expectedSalaryMin = 70000;
        int expectedSalaryMax = 110000;
        var expectedJobType = JobType.FULL_TIME;
        var expectedPostedAt = Instant.now();
        var expectedCompanyName = "TechCorp";

        var employer = new Employer();
        employer.setCompanyName(expectedCompanyName);

        var job = new Job();
        job.setId(expectedId);
        job.setTitle(expectedTitle);
        job.setDescription(expectedDescription);
        job.setLocation(expectedLocation);
        job.setSalaryMin(expectedSalaryMin);
        job.setSalaryMax(expectedSalaryMax);
        job.setJobType(expectedJobType);
        job.setPostedAt(expectedPostedAt);

        employer.addJob(job);

        JobResponseDTO dto = JobResponseDTO.fromJob(job);

        assertNotNull(dto);
        assertEquals(expectedId, dto.getId());
        assertEquals(expectedTitle, dto.getTitle());
        assertEquals(expectedDescription, dto.getDescription());
        assertEquals(expectedLocation, dto.getLocation());
        assertEquals(expectedSalaryMin, dto.getSalaryMin());
        assertEquals(expectedSalaryMax, dto.getSalaryMax());
        assertEquals(expectedJobType, dto.getJobType());
        assertEquals(expectedPostedAt, dto.getPostedAt());
        assertEquals(expectedCompanyName, dto.getCompanyName());
    }
}