package com.minhhai.newsmanagement.service;

import com.minhhai.newsmanagement.dto.request.LoginRequest;
import com.minhhai.newsmanagement.dto.response.AuthResponse;
import com.minhhai.newsmanagement.entity.UserAccount;
import com.minhhai.newsmanagement.repository.UserRepository;
import com.minhhai.newsmanagement.security.JwtUtil;
import com.minhhai.newsmanagement.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void testLoginSuccess() {
        UserAccount user = new UserAccount();
        user.setId(1L);
        user.setUsername("Admin");
        user.setPasswordHash("$2a$10$hashedPassword");
        user.setName("Admin User");
        user.setRole(1);
        user.setStatus(1);

        when(userRepository.findByUsername("Admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Admin", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("Admin", 1, 1L)).thenReturn("mock.jwt.token");

        LoginRequest request = new LoginRequest("Admin", "Admin");
        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals("Admin", response.username());
        assertEquals(1, response.role());
    }

    @Test
    void testLoginWrongPassword() {
        UserAccount user = new UserAccount();
        user.setUsername("Admin");
        user.setPasswordHash("$2a$10$hashedPassword");
        user.setStatus(1);

        when(userRepository.findByUsername("Admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "$2a$10$hashedPassword")).thenReturn(false);

        LoginRequest request = new LoginRequest("Admin", "wrongPass");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("unknown", "pass");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testLoginUserInactive() {
        UserAccount user = new UserAccount();
        user.setUsername("blocked");
        user.setPasswordHash("$2a$10$hashedPassword");
        user.setStatus(0); // Inactive

        when(userRepository.findByUsername("blocked")).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest("blocked", "pass");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(request));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
