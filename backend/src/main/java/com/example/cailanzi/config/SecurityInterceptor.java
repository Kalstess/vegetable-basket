package com.example.cailanzi.config;

import com.example.cailanzi.entity.User;
import com.example.cailanzi.service.AuthService;
import com.example.cailanzi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityInterceptor implements HandlerInterceptor {

    private final AuthService authService;
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 允许OPTIONS请求（CORS预检）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 公开接口不需要认证
        // 注意：由于设置了 context-path=/api，getRequestURI() 返回的路径不包含 /api
        String path = request.getRequestURI();
        log.info("Request path: {}", path);
        
        if (path.startsWith("/auth/login") || 
            path.startsWith("/company-registration/register") ||
            path.startsWith("/swagger") || 
            path.startsWith("/v3/api-docs")) {
            log.info("Public path, allowing access: {}", path);
            return true;
        }

        // 验证token
        String authHeader = request.getHeader("Authorization");
        log.info("Authorization header: {}", authHeader != null ? "present" : "missing");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未授权\"}");
            return false;
        }

        String token = authHeader.substring(7);
        log.info("Token extracted, validating...");
        
        if (!authService.validateToken(token)) {
            log.warn("Token validation failed for path: {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\"}");
            return false;
        }
        
        log.info("Token validated successfully");

        // 将用户信息存储到request属性中，供后续使用
        try {
            String username = authService.getUsernameFromToken(token);
            if (username == null || username.isEmpty()) {
                log.warn("Username is null or empty from token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token无效\"}");
                return false;
            }
            
            log.info("Username extracted from token: {}", username);
            
            Optional<User> userOptional = userService.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (!user.getIsActive()) {
                    log.warn("User is disabled: {}", username);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"用户已被禁用\"}");
                    return false;
                }
                // 确保用户信息正确设置到request中
                request.setAttribute("currentUser", user);
                log.info("User loaded successfully: {}, role: {}", username, user.getRole());
            } else {
                log.warn("User not found: {}", username);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"用户不存在\"}");
                return false;
            }
        } catch (Exception e) {
            log.error("Error loading user: ", e);
            // 打印详细错误信息以便调试
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            response.getWriter().write("{\"code\":500,\"message\":\"加载用户信息失败: " + errorMsg + "\"}");
            return false;
        }

        return true;
    }
}

