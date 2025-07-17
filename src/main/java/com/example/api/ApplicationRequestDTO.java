package com.example.api;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class ApplicationRequestDTO {

    @NotNull
    private Long jobId;

    private String coverLetter;
    private String resumeLink;
}
