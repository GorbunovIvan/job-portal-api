package com.example.api;

import com.example.model.Job;
import com.example.model.enums.JobType;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class JobResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String location;
    private Integer salaryMin;
    private Integer salaryMax;
    private JobType jobType;
    private Instant postedAt;
    private String companyName;

    public static JobResponseDTO fromJob(Job job) {

        var dto = new JobResponseDTO();

        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setSalaryMin(job.getSalaryMin());
        dto.setSalaryMax(job.getSalaryMax());
        dto.setJobType(job.getJobType());
        dto.setPostedAt(job.getPostedAt());

//        if (job.getEmployer() != null) {
            dto.setCompanyName(job.getEmployer().getCompanyName());
//        }

        return dto;
    }
}
