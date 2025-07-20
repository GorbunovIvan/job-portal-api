package com.example.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class ApplicationRequestAndUserIdDTO {

    @NotNull
    private Long userId;

    @Valid
    private ApplicationRequestDTO applicationRequestDTO;
}
