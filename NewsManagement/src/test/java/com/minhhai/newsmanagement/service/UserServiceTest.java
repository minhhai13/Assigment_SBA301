package com.minhhai.newsmanagement.service;

import com.minhhai.newsmanagement.dto.request.UserRequest;
import com.minhhai.newsmanagement.dto.response.UserResponse;
import com.minhhai.newsmanagement.entity.NewsArticle;
import com.minhhai.newsmanagement.entity.UserAccount;
import com.minhhai.newsmanagement.repository.UserRepository;
import com.minhhai.newsmanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void testGetAllUsers() {
        UserAccount u = new UserAccount();
        u.setId(1L);
        u.setUsername("Admin");
        u.setName("Minh Hai");
        u.setRole(1);
        u.setStatus(1);

        when(userRepository.findAll()).thenReturn(List.of(u));

        List<UserResponse> list = userService.getAllUsers();
        assertEquals(1, list.size());
        assertEquals("Admin", list.get(0).username());
    }

    @Test
    void testGetUserById_Success() {
        UserAccount u = new UserAccount();
        u.setId(1L);
        u.setUsername("Admin");
        u.setName("Minh Hai");
        u.setRole(1);
        u.setStatus(1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        UserResponse res = userService.getUserById(1L);
        assertNotNull(res);
        assertEquals("Admin", res.username());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.getUserById(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCreateUser_Success() {
        UserRequest req = new UserRequest("NewUser", "pass123", "New Name", 2, 1);

        when(userRepository.existsByUsername("NewUser")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashedPass123");

        UserAccount saved = new UserAccount();
        saved.setId(10L);
        saved.setUsername("NewUser");
        saved.setName("New Name");
        saved.setRole(2);
        saved.setStatus(1);

        when(userRepository.save(any(UserAccount.class))).thenReturn(saved);

        UserResponse res = userService.createUser(req);
        assertNotNull(res);
        assertEquals("NewUser", res.username());
        assertEquals(10L, res.id());
    }

    @Test
    void testCreateUser_DuplicateUsername() {
        UserRequest req = new UserRequest("Admin", "pass123", "New Name", 2, 1);
        when(userRepository.existsByUsername("Admin")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.createUser(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testUpdateUser_Success() {
        UserAccount existing = new UserAccount();
        existing.setId(1L);
        existing.setUsername("Admin");
        existing.setName("Old Name");
        existing.setRole(2);
        existing.setStatus(1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(UserAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        UserRequest req = new UserRequest("Admin", null, "Updated Name", 1, 1);
        UserResponse res = userService.updateUser(1L, req);

        assertEquals("Updated Name", res.name());
        assertEquals(1, res.role());
    }

    @Test
    void testDeleteUser_Success() {
        UserAccount existing = new UserAccount();
        existing.setId(1L);
        existing.setNewsArticles(Collections.emptyList());

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).delete(existing);
    }

    @Test
    void testDeleteUser_HasArticles_ThrowsBadRequest() {
        UserAccount existing = new UserAccount();
        existing.setId(1L);
        NewsArticle article = new NewsArticle();
        existing.setNewsArticles(List.of(article));

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.deleteUser(1L));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
