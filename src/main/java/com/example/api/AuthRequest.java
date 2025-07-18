package com.example.api;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class AuthRequest {

    @NotNull
    private String email;

    private String password;
}

