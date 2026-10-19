package com.minhhai.newsmanagement.service.impl;

import com.minhhai.newsmanagement.dto.request.LoginRequest;
import com.minhhai.newsmanagement.dto.response.AuthResponse;
import com.minhhai.newsmanagement.entity.UserAccount;
import com.minhhai.newsmanagement.repository.UserRepository;
import com.minhhai.newsmanagement.security.JwtUtil;
import com.minhhai.newsmanagement.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Tìm tài khoản theo username
        UserAccount user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản hoặc mật khẩu không chính xác"));

        // 2. Kiểm tra trạng thái tài khoản (1: Active, 0: Inactive)
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản đã bị vô hiệu hóa");
        }

        // 3. Kiểm tra mật khẩu (hỗ trợ cả mật khẩu băm BCrypt và văn bản gốc cho dữ liệu seed ban đầu)
        boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPasswordHash())
                || request.password().equals(user.getPasswordHash());

        if (!passwordMatches) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản hoặc mật khẩu không chính xác");
        }

        // Nếu mật khẩu trong DB đang là plaintext cũ, tự động băm lại bằng BCrypt để nâng cao bảo mật
        if (request.password().equals(user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
            userRepository.save(user);
        }

        // 4. Tạo token JWT
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());

        // 5. Trả về thông tin đăng nhập và token
        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getRole(),
                user.getStatus()
        );
    }
}
