package com.example.cailanzi.service;

import com.example.cailanzi.dto.LoginRequest;
import com.example.cailanzi.dto.LoginResponse;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.repository.UserRepository;
import com.example.cailanzi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        
        if (userOptional.isEmpty()) {
            log.warn("Login failed: User not found - {}", request.getUsername());
            throw new IllegalArgumentException("用户名或密码错误");
        }

        User user = userOptional.get();
        log.debug("User found: id={}, role={}, isActive={}", user.getId(), user.getRole(), user.getIsActive());

        if (!user.getIsActive()) {
            log.warn("Login failed: User is disabled - {}", request.getUsername());
            throw new IllegalArgumentException("用户已被禁用");
        }

        String encodedPassword = encodePassword(request.getPassword());
        log.debug("Password encoded, comparing with stored password");
        
        if (!encodedPassword.equals(user.getPassword())) {
            log.warn("Login failed: Password mismatch for user - {}", request.getUsername());
            throw new IllegalArgumentException("用户名或密码错误");
        }

        String token = JwtUtil.generateToken(user.getUsername());
        log.info("Login successful for user: {}", request.getUsername());

        return new LoginResponse(
            token,
            user.getUsername(),
            user.getNickname() != null ? user.getNickname() : user.getUsername(),
            user.getRole().name()
        );
    }

    public boolean validateToken(String token) {
        return JwtUtil.validateToken(token);
    }

    public String getUsernameFromToken(String token) {
        return JwtUtil.getUsernameFromToken(token);
    }

    // 密码编码（使用SHA-256）
    private String encodePassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
}

