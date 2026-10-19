package com.minhhai.newsmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minhhai.newsmanagement.dto.request.UserRequest;
import com.minhhai.newsmanagement.dto.response.UserResponse;
import com.minhhai.newsmanagement.exception.GlobalExceptionHandler;
import com.minhhai.newsmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetUsers_Success() throws Exception {
        UserResponse u1 = new UserResponse(1L, "Admin", "Đỗ Minh Hải", 1, 1);
        when(userService.getAllUsers()).thenReturn(List.of(u1));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Admin"))
                .andExpect(jsonPath("$[0].name").value("Đỗ Minh Hải"));
    }

    @Test
    void testGetUserById_Success() throws Exception {
        UserResponse u1 = new UserResponse(1L, "Admin", "Đỗ Minh Hải", 1, 1);
        when(userService.getUserById(1L)).thenReturn(u1);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Admin"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với ID: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy người dùng với ID: 99"));
    }

    @Test
    void testCreateUser_Success() throws Exception {
        UserRequest request = new UserRequest("StaffNew", "123456", "Staff New Name", 2, 1);
        UserResponse created = new UserResponse(2L, "StaffNew", "Staff New Name", 2, 1);

        when(userService.createUser(any(UserRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("StaffNew"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        UserRequest request = new UserRequest("StaffNew", null, "Updated Name", 2, 1);
        UserResponse updated = new UserResponse(2L, "StaffNew", "Updated Name", 2, 1);

        when(userService.updateUser(eq(2L), any(UserRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(2L);

        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isOk());
    }
}
