package com.minhhai.newsmanagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Khóa bí mật dùng để ký và giải mã JWT (tối thiểu 256 bits = 32 ký tự)
    @Value("${jwt.secret:NewsManagementSuperSecretKeyForJwtAuthenticationWith256BitsMinimumLength2026}")
    private String jwtSecret;

    // Thời gian hết hạn của token (mặc định: 24 giờ = 86400000 ms)
    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    // Tạo SecretKey chuẩn HMAC-SHA từ chuỗi bí mật
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Tạo JWT token chứa thông tin username, role và userId
     */
    public String generateToken(String username, Integer role, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Trích xuất toàn bộ Claims (payload) từ JWT token
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Lấy username từ subject của JWT
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * Lấy role từ payload của JWT (1: Admin, 2: Staff)
     */
    public Integer getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", Integer.class);
    }

    /**
     * Lấy userId từ payload của JWT
     */
    public Long getUserIdFromToken(String token) {
        Object val = getClaimsFromToken(token).get("userId");
        if (val instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    /**
     * Kiểm tra token có hợp lệ và chưa hết hạn hay không
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // Token không hợp lệ, bị giả mạo hoặc đã hết hạn
            return false;
        }
    }
}
