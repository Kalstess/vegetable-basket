package com.example.cailanzi.controller;

import com.example.cailanzi.dto.CompanyRegisterRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 企业自助注册与商务委审批相关接口。
 */
@Slf4j
@RestController
@RequestMapping("/company-registration")
@RequiredArgsConstructor
@Tag(name = "企业注册与审批", description = "企业自助注册、商务委审批相关接口")
public class CompanyRegistrationController {

    private final CompanyService companyService;
    private final UserService userService;
    private final AuthService authService;
    private final AuditLogService auditLogService;

    @PostMapping("/register")
    @Operation(summary = "企业自助注册（待商务委审核）")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ResponseMessage<String>> registerCompany(@Valid @RequestBody CompanyRegisterRequest request) {
        Company savedCompany = null;
        try {
            // 1. 创建企业，状态为 PENDING
            Company company = new Company();
            company.setName(request.getCompanyName());
            company.setAddress(request.getAddress());
            company.setLegalPersonName(request.getLegalPersonName());
            company.setLegalPersonPhone(request.getLegalPersonPhone());
            company.setFreightPassContactName(request.getFreightPassContactName());
            company.setFreightPassContactPhone(request.getFreightPassContactPhone());
            if (request.getCompanyType() != null) {
                try {
                    company.setCompanyType(Company.CompanyType.valueOf(request.getCompanyType()));
                } catch (IllegalArgumentException e) {
                    // 忽略非法值，交由后续管理界面修正
                }
            }
            company.setBusinessScope(request.getBusinessScope());
            company.setStatus(Company.CompanyStatus.PENDING);

            savedCompany = companyService.save(company);

            // 2. 创建企业管理员账号，默认禁用，待审核通过后启用
            User adminUser = new User();
            adminUser.setUsername(request.getAdminUsername());
            adminUser.setPassword(request.getAdminPassword());
            adminUser.setNickname(Optional.ofNullable(request.getAdminNickname()).orElse(request.getAdminUsername()));
            adminUser.setEmail(request.getAdminEmail());
            adminUser.setPhone(request.getAdminPhone());
            // 明确设置角色为企业管理员，确保不会被默认值覆盖
            adminUser.setRole(User.UserRole.COMPANY_ADMIN);
            adminUser.setCompany(savedCompany);
            adminUser.setIsActive(false);

            log.info("创建企业管理员账号: username={}, role={}, companyId={}", 
                    adminUser.getUsername(), adminUser.getRole(), savedCompany.getId());
            User createdUser = userService.create(adminUser);
            log.info("企业管理员账号创建完成: userId={}, role={}", 
                    createdUser.getId(), createdUser.getRole());

            auditLogService.log(null, "COMPANY", savedCompany.getId(), "REGISTER",
                    "企业自助注册，名称=" + savedCompany.getName());

            return ResponseEntity.ok(ResponseMessage.success("提交成功，待商务委审核"));
        } catch (Exception e) {
            // 如果创建用户失败，回滚企业数据
            if (savedCompany != null && savedCompany.getId() != null) {
                try {
                    companyService.deleteById(savedCompany.getId());
                } catch (Exception deleteException) {
                    // 记录日志但不抛出异常
                    log.error("回滚企业数据失败: {}", deleteException.getMessage());
                }
            }
            throw e; // 重新抛出异常，让事务回滚
        }
    }

    @PostMapping("/{companyId}/approve")
    @Operation(summary = "商务委审核通过企业注册")
    public ResponseEntity<ResponseMessage<String>> approveCompany(
            @PathVariable Long companyId,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) Long existingCompanyId,
            HttpServletRequest request) {
        User operator = getCurrentUser(request);
        if (operator.getRole() != User.UserRole.BUSINESS_COMMISSION && operator.getRole() != User.UserRole.ADMIN) {
            throw new IllegalArgumentException("无审批权限");
        }

        Company company = companyService.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("企业不存在"));
        
        final Company targetCompany;
        // 如果提供了 existingCompanyId，说明要关联到已有企业
        if (existingCompanyId != null && !existingCompanyId.equals(companyId)) {
            // 关联到已有企业
            targetCompany = companyService.findById(existingCompanyId)
                    .orElseThrow(() -> new IllegalArgumentException("目标企业不存在"));
            
            final Company finalTargetCompany = targetCompany;
            // 将待审批企业的管理员账号关联到已有企业
            // 同时确保角色正确设置为 COMPANY_ADMIN（修复注册时可能出现的角色设置问题）
            userService.findByCompanyId(companyId).forEach(user -> {
                if (user.getRole() == User.UserRole.COMPANY_ADMIN || user.getRole() == User.UserRole.COMPANY_USER) {
                    // 如果是企业管理员或企业普通用户，都设置为企业管理员并启用
                    User updateUser = new User();
                    updateUser.setIsActive(true);
                    updateUser.setCompany(finalTargetCompany);
                    updateUser.setRole(User.UserRole.COMPANY_ADMIN);
                    userService.update(user.getId(), updateUser);
                }
            });
            
            // 删除待审批的企业记录（因为已经关联到已有企业）
            companyService.deleteById(companyId);
        } else {
            // 直接审批通过，创建新企业
            company.setStatus(Company.CompanyStatus.APPROVED);
            company.setAuditRemark(remark);
            company.setAuditedAt(java.time.LocalDateTime.now());
            targetCompany = companyService.save(company);

            // 启用该企业下的企业管理员账号（只更新 isActive，不更新密码）
            // 同时确保角色正确设置为 COMPANY_ADMIN（修复注册时可能出现的角色设置问题）
            userService.findByCompanyId(companyId).forEach(user -> {
                if (user.getRole() == User.UserRole.COMPANY_ADMIN || user.getRole() == User.UserRole.COMPANY_USER) {
                    // 如果是企业管理员或企业普通用户，都设置为企业管理员并启用
                    User updateUser = new User();
                    updateUser.setIsActive(true);
                    updateUser.setRole(User.UserRole.COMPANY_ADMIN);
                    userService.update(user.getId(), updateUser);
                }
            });
        }

        auditLogService.log(operator, "COMPANY", targetCompany.getId(), "APPROVE",
                "审批通过，备注=" + remark + (existingCompanyId != null ? "，关联到已有企业ID=" + existingCompanyId : ""));

        return ResponseEntity.ok(ResponseMessage.success("审批通过"));
    }

    @PostMapping("/{companyId}/reject")
    @Operation(summary = "商务委驳回企业注册")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ResponseMessage<String>> rejectCompany(
            @PathVariable Long companyId,
            @RequestParam(required = false) String remark,
            HttpServletRequest request) {
        User operator = getCurrentUser(request);
        if (operator.getRole() != User.UserRole.BUSINESS_COMMISSION && operator.getRole() != User.UserRole.ADMIN) {
            throw new IllegalArgumentException("无审批权限");
        }

        Company company = companyService.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("企业不存在"));
        
        // 记录企业名称用于审计日志（删除后无法获取）
        String companyName = company.getName();
        
        // 先删除该企业下的所有用户（特别是企业管理员）
        List<User> companyUsers = userService.findByCompanyId(companyId);
        for (User user : companyUsers) {
            try {
                userService.deleteById(user.getId());
                log.info("删除被驳回企业的用户: userId={}, username={}", user.getId(), user.getUsername());
            } catch (Exception e) {
                log.warn("删除用户失败，继续删除企业: userId={}, error={}", user.getId(), e.getMessage());
            }
        }
        
        // 然后删除企业本身
        try {
            companyService.deleteById(companyId);
            log.info("删除被驳回的企业: companyId={}, name={}", companyId, companyName);
        } catch (Exception e) {
            log.error("删除企业失败: companyId={}, error={}", companyId, e.getMessage());
            throw new RuntimeException("删除企业失败: " + e.getMessage(), e);
        }

        // 记录审计日志（使用已保存的企业名称）
        auditLogService.log(operator, "COMPANY", companyId, "REJECT",
                "审批驳回并删除，企业名称=" + companyName + "，备注=" + remark);

        return ResponseEntity.ok(ResponseMessage.success("已驳回并删除相关数据"));
    }

    private User getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = authService.getUsernameFromToken(token);
            return userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        }
        throw new IllegalArgumentException("未授权");
    }
}


