package com.example.api;

import com.example.model.Application;
import com.example.model.enums.ApplicationStatus;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class ApplicationResponseDTO {

    private Long id;
    private String jobTitle;
    private ApplicationStatus status;
    private Instant appliedAt;

    public static ApplicationResponseDTO fromApplication(Application app) {
        var dto = new ApplicationResponseDTO();
        dto.setId(app.getId());
        dto.setJobTitle(app.getJob().getTitle());
        dto.setAppliedAt(app.getAppliedAt());
        dto.setStatus(app.getStatus());
        return dto;
    }
}
