package com.example.cailanzi.controller;

import com.example.cailanzi.dto.LoginRequest;
import com.example.cailanzi.dto.LoginResponse;
import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseEntity<ResponseMessage<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ResponseMessage.success("登录成功", response));
    }

    @GetMapping("/validate")
    @Operation(summary = "验证token")
    public ResponseEntity<ResponseMessage<Boolean>> validateToken(@RequestHeader("Authorization") String token) {
        // 移除 "Bearer " 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        boolean valid = authService.validateToken(token);
        return ResponseEntity.ok(ResponseMessage.success(valid));
    }
}

