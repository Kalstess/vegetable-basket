package com.example.cailanzi.service;

import com.example.cailanzi.dto.LoginRequest;
import com.example.cailanzi.dto.LoginResponse;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.repository.UserRepository;
import com.example.cailanzi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    private static final int LOCK_MINUTES = 30;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            log.warn("Login failed: User not found - {}", request.getUsername());
            throw new IllegalArgumentException("用户名或密码错误");
        }

        User user = userOptional.get();
        log.debug("User found: id={}, role={}, isActive={}", user.getId(), user.getRole(), user.getIsActive());

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            log.warn("Login failed: User is disabled - {}", request.getUsername());
            throw new IllegalArgumentException("用户已被禁用");
        }

        // 简单登录安全控制：连续多次失败后短时间锁定
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            log.warn("Login failed: User is temporarily locked - {}", request.getUsername());
            throw new IllegalArgumentException("账号已被暂时锁定，请稍后再试");
        }

        boolean passwordMatched = matchesPassword(request.getPassword(), user.getPassword());
        if (!passwordMatched) {
            int failCount = Optional.ofNullable(user.getLoginFailCount()).orElse(0) + 1;
            user.setLoginFailCount(failCount);
            if (failCount >= MAX_LOGIN_FAIL_COUNT) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
                log.warn("User locked due to too many failed attempts: {}", request.getUsername());
            }
            userRepository.save(user);
            log.warn("Login failed: Password mismatch for user - {}", request.getUsername());
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 登录成功：重置失败次数，记录最后登录时间
        user.setLoginFailCount(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = JwtUtil.generateToken(user.getUsername());
        log.info("Login successful for user: {}", request.getUsername());

        Long companyId = user.getCompany() != null ? user.getCompany().getId() : null;
        return new LoginResponse(
                token,
                user.getUsername(),
                user.getNickname() != null ? user.getNickname() : user.getUsername(),
                user.getRole().name(),
                companyId
        );
    }

    public boolean validateToken(String token) {
        return JwtUtil.validateToken(token);
    }

    public String getUsernameFromToken(String token) {
        return JwtUtil.getUsernameFromToken(token);
    }

    /**
     * 使用 BCrypt 为主，兼容旧的 SHA-256 存储：
     * - 若密码为旧的 SHA-256 且匹配，则自动迁移为 BCrypt 存储。
     */
    public boolean matchesPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        // 优先尝试 BCrypt
        try {
            if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
                return passwordEncoder.matches(rawPassword, storedPassword);
            }
        } catch (Exception e) {
            log.error("BCrypt match error", e);
        }

        // 兼容旧 SHA-256：如果匹配，则在调用处负责迁移
        String sha256 = encodeSha256(rawPassword);
        return sha256.equals(storedPassword);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // 兼容旧逻辑的 SHA-256
    private String encodeSha256(String password) {
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


