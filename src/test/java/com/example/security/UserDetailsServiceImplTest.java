package com.example.security;

import com.example.model.user.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsService userDetailsServiceImpl;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void shouldReturnUserWhenLoadUserByUsername() {

        var userMock = new User();
        userMock.setId(1L);
        userMock.setEmail("some-email@mail.com");

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(userMock));

        var result = userDetailsServiceImpl.loadUserByUsername(userMock.getEmail());

        assertNotNull(result);
        assertEquals(userMock, result);
        assertEquals(userMock.getUsername(), result.getUsername());
    }

    @Test
    void shouldThrowNotFoundWhenLoadUserByUsername() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userDetailsServiceImpl.loadUserByUsername("email@mail.com"));
    }
}