package com.minhhai.newsmanagement.service.impl;

import com.minhhai.newsmanagement.dto.request.UserRequest;
import com.minhhai.newsmanagement.dto.response.UserResponse;
import com.minhhai.newsmanagement.entity.UserAccount;
import com.minhhai.newsmanagement.repository.UserRepository;
import com.minhhai.newsmanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy người dùng với ID: " + id
                ));
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tên đăng nhập (username) đã tồn tại: " + request.username()
            );
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.username());

        // Nếu người dùng không nhập mật khẩu, đặt mật khẩu mặc định là "123456"
        String rawPassword = (request.password() != null && !request.password().isBlank())
                ? request.password()
                : "123456";
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        user.setName(request.name());
        user.setRole(request.role() != null ? request.role() : 2); // Mặc định role = 2 (Staff)
        user.setStatus(request.status() != null ? request.status() : 1); // Mặc định status = 1 (Active)

        UserAccount saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy người dùng với ID: " + id
                ));

        // Cập nhật họ tên nếu có
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }

        // Cập nhật phân quyền nếu có
        if (request.role() != null) {
            user.setRole(request.role());
        }

        // Cập nhật trạng thái nếu có
        if (request.status() != null) {
            user.setStatus(request.status());
        }

        // Cập nhật mật khẩu mới nếu người dùng nhập mật khẩu mới
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        UserAccount updated = userRepository.save(user);
        return UserResponse.from(updated);
    }

    @Override
    public void deleteUser(Long id) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy người dùng với ID: " + id
                ));

        // Kiểm tra ràng buộc: nếu user đã đăng bài viết tin tức thì không cho xóa cứng để bảo đảm toàn vẹn dữ liệu
        if (user.getNewsArticles() != null && !user.getNewsArticles().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Không thể xóa người dùng đã có bài viết tin tức. Vui lòng chuyển trạng thái sang Inactive."
            );
        }

        userRepository.delete(user);
    }
}
