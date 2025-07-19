package com.example.security;

import com.example.model.user.Role;
import com.example.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {

        jwtUtil = new JwtUtil();

        // Manually inject the @Value fields
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "my-very-secret-key-for-testing-only-123456");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 5);  // 5 minutes

        var user = new User();
        user.setId(2L);
        user.setEmail("test@example.com");
        user.setPassword("test-password");
        user.setRoles(Set.of(new Role(3, "APPLICANT")));

        userDetails = user;
    }

    @Test
    void shouldReturnTrueWhenGenerateAndValidateToken() {

        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token, "Token should not be null");
        String extractedUsername = jwtUtil.extractUsernameFromToken(token);
        assertEquals(userDetails.getUsername(), extractedUsername);

        var result = jwtUtil.isTokenValid(token, userDetails);
        assertTrue(result, "Token should be valid for the user");
    }

    @Test
    void shouldThrowWhenExtractUsernameFromToken() {
        assertThrows(Exception.class, () -> jwtUtil.extractUsernameFromToken("fake.invalid.token"));
    }

    @Test
    void shouldReturnFalseWhenIsTokenValid() throws InterruptedException {

        // Short-lived token for this test
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 0);  // 0 minutes = immediate expiration

        String token = jwtUtil.generateToken(userDetails);

        Thread.sleep(500);  // Allow token to become expired

        var result = jwtUtil.isTokenValid(token, userDetails);
        assertFalse(result, "Token should be expired");
    }
}
