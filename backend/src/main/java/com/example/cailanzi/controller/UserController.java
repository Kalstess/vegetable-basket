package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.service.AuthService;
import com.example.cailanzi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理API")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    @Operation(summary = "获取所有用户列表（仅管理员）")
    public ResponseEntity<ResponseMessage<List<User>>> getAllUsers(HttpServletRequest request) {
        // 验证管理员权限
        String username = getUsernameFromRequest(request);
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (currentUser.getRole() != User.UserRole.ADMIN) {
            throw new IllegalArgumentException("无权限访问");
        }

        List<User> users = userService.findAll();
        return ResponseEntity.ok(ResponseMessage.success(users));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户详情")
    public ResponseEntity<ResponseMessage<User>> getUserById(@PathVariable Long id, HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 只能查看自己的信息，或管理员可以查看所有
        if (!currentUser.getRole().equals(User.UserRole.ADMIN) && !currentUser.getId().equals(id)) {
            throw new IllegalArgumentException("无权限访问");
        }

        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return ResponseEntity.ok(ResponseMessage.success(user));
    }

    @PostMapping
    @Operation(summary = "创建用户（仅管理员）")
    public ResponseEntity<ResponseMessage<User>> createUser(@Valid @RequestBody User user, HttpServletRequest request) {
        // 验证管理员权限
        String username = getUsernameFromRequest(request);
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (currentUser.getRole() != User.UserRole.ADMIN) {
            throw new IllegalArgumentException("无权限访问");
        }

        // 记录接收到的用户数据（用于调试）
        System.out.println("Received user data: username=" + user.getUsername() + ", role=" + user.getRole());

        User created = userService.create(user);
        return ResponseEntity.ok(ResponseMessage.success("用户创建成功", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息（管理员可更新所有用户，普通用户只能更新自己）")
    public ResponseEntity<ResponseMessage<User>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User userDetails,
            HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 非管理员只能更新自己的信息
        if (!currentUser.getRole().equals(User.UserRole.ADMIN) && !currentUser.getId().equals(id)) {
            throw new IllegalArgumentException("无权限访问");
        }

        User updated = userService.update(id, userDetails);
        return ResponseEntity.ok(ResponseMessage.success("用户更新成功", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户（仅管理员）")
    public ResponseEntity<ResponseMessage<Void>> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        // 验证管理员权限
        String username = getUsernameFromRequest(request);
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (currentUser.getRole() != User.UserRole.ADMIN) {
            throw new IllegalArgumentException("无权限访问");
        }

        userService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("用户删除成功", null));
    }

    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息")
    public ResponseEntity<ResponseMessage<User>> getCurrentUser(HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return ResponseEntity.ok(ResponseMessage.success(user));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新当前用户个人信息")
    public ResponseEntity<ResponseMessage<User>> updateProfile(@Valid @RequestBody User userDetails, HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        User updated = userService.updateProfile(username, userDetails);
        return ResponseEntity.ok(ResponseMessage.success("个人信息更新成功", updated));
    }

    private String getUsernameFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return authService.getUsernameFromToken(token);
        }
        throw new IllegalArgumentException("未授权");
    }
}

