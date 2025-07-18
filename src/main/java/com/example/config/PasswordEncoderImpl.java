package com.example.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderImpl extends BCryptPasswordEncoder {

    public PasswordEncoderImpl() {
        super(10);
    }
}
