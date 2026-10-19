package com.minhhai.newsmanagement.service;

import com.minhhai.newsmanagement.dto.request.UserRequest;
import com.minhhai.newsmanagement.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse createUser(UserRequest request);
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
}
