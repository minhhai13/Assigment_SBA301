package com.minhhai.newsmanagement.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "ThisIsASecretKeyWithMoreThan256BitsForTestingPurposesOnly1234567890!");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 3600000L); // 1 hour
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtil.generateToken("testuser", 1, 100L);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("testuser", jwtUtil.getUsernameFromToken(token));
        assertEquals(1, jwtUtil.getRoleFromToken(token));
        assertEquals(100L, jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }
}
