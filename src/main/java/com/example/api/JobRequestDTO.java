package com.example.api;

import com.example.model.Job;
import com.example.model.enums.JobType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class JobRequestDTO {

    @NotBlank
    private String title;

    private String description;
    private String location;
    private Integer salaryMin;
    private Integer salaryMax;
    private JobType jobType;

    public static Job toJob(JobRequestDTO dto) {

        var job = new Job();

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setJobType(dto.getJobType());

        return job;
    }
}

