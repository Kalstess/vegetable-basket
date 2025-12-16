package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.entity.Company;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.service.AuditLogService;
import com.example.cailanzi.service.AuthService;
import com.example.cailanzi.service.CompanyService;
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
    private final CompanyService companyService;
    private final AuditLogService auditLogService;

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
    @Operation(summary = "创建用户（按角色分层权限）")
    public ResponseEntity<ResponseMessage<User>> createUser(@Valid @RequestBody User user, HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 账号创建分层控制：
        // - ADMIN：可以创建任意角色；
        // - BUSINESS_COMMISSION：仅能创建商务委内部账号；
        // - COMPANY / COMPANY_ADMIN：只能在本企业范围内创建 COMPANY_USER / DRIVER，且强制绑定本企业；
        // 其他角色无创建权限。
        switch (currentUser.getRole()) {
            case ADMIN -> {
                // 管理员不做额外限制
            }
            case BUSINESS_COMMISSION -> {
                if (user.getRole() == null) {
                    user.setRole(User.UserRole.BUSINESS_COMMISSION);
                }
                if (user.getRole() != User.UserRole.BUSINESS_COMMISSION) {
                    throw new IllegalArgumentException("商务委用户只能创建商务委内部账号");
                }
            }
            case COMPANY, COMPANY_ADMIN -> {
                if (user.getRole() == null) {
                    user.setRole(User.UserRole.COMPANY_USER);
                }
                if (user.getRole() != User.UserRole.COMPANY_USER && user.getRole() != User.UserRole.DRIVER) {
                    throw new IllegalArgumentException("企业管理员只能创建本企业普通用户或司机用户");
                }
                // 强制限定在本企业范围内
                Company company = currentUser.getCompany();
                if (company == null || company.getId() == null) {
                    throw new IllegalArgumentException("当前企业管理员未绑定企业，无法创建账号");
                }
                user.setCompany(companyService.findById(company.getId())
                        .orElseThrow(() -> new IllegalArgumentException("企业不存在")));
            }
            default -> throw new IllegalArgumentException("无权限创建用户");
        }

        // 记录接收到的用户数据（用于调试）
        System.out.println("Received user data: username=" + user.getUsername() + ", role=" + user.getRole());

        User created = userService.create(user);
        auditLogService.log(currentUser, "USER", created.getId(), "CREATE",
                "创建用户，username=" + created.getUsername() + ", role=" + created.getRole());
        return ResponseEntity.ok(ResponseMessage.success("用户创建成功", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息（管理员可更新所有用户，普通/企业管理员有限制）")
    public ResponseEntity<ResponseMessage<User>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User userDetails,
            HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 权限规则：
        // - ADMIN：可以更新任意用户信息；
        // - BUSINESS_COMMISSION：仅能更新商务委内部账号；
        // - COMPANY / COMPANY_ADMIN：可以更新本企业下的 COMPANY_USER / DRIVER；
        // - 其他用户：只能更新自己的个人信息（/profile 接口），不通过此接口。
        if (currentUser.getRole() == User.UserRole.ADMIN) {
            // ok
        } else if (currentUser.getRole() == User.UserRole.BUSINESS_COMMISSION) {
            User target = userService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            if (target.getRole() != User.UserRole.BUSINESS_COMMISSION) {
                throw new IllegalArgumentException("无权限更新其他类型账号");
            }
        } else if (currentUser.getRole() == User.UserRole.COMPANY
                || currentUser.getRole() == User.UserRole.COMPANY_ADMIN) {
            User target = userService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            if (target.getCompany() == null || currentUser.getCompany() == null
                    || !target.getCompany().getId().equals(currentUser.getCompany().getId())) {
                throw new IllegalArgumentException("只能管理本企业用户");
            }
            if (target.getRole() != User.UserRole.COMPANY_USER && target.getRole() != User.UserRole.DRIVER) {
                throw new IllegalArgumentException("只能更新本企业普通用户或司机用户");
            }
        } else {
            throw new IllegalArgumentException("无权限访问");
        }

        User updated = userService.update(id, userDetails);
        auditLogService.log(currentUser, "USER", updated.getId(), "UPDATE",
                "更新用户信息，username=" + updated.getUsername());
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
        auditLogService.log(currentUser, "USER", id, "DELETE", "删除用户");
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

