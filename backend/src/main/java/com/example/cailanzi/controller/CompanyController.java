package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.entity.Company;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.service.CompanyService;
import com.example.cailanzi.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
@Tag(name = "企业管理", description = "企业基本信息管理API")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    @Operation(summary = "获取企业列表（管理员查看所有，企业用户只能查看自己的）")
    public ResponseEntity<ResponseMessage<List<Company>>> getAllCompanies(HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        List<Company> companies;
        
        if (currentUser.getRole() == User.UserRole.ADMIN) {
            companies = companyService.findAll();
        } else if (currentUser.getRole() == User.UserRole.COMPANY && currentUser.getCompany() != null) {
            companies = List.of(currentUser.getCompany());
        } else {
            companies = List.of();
        }
        
        return ResponseEntity.ok(ResponseMessage.success(companies));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取企业详情")
    public ResponseEntity<ResponseMessage<Company>> getCompanyById(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        // 企业用户只能查看自己的企业
        if (currentUser.getRole() == User.UserRole.COMPANY && 
            (currentUser.getCompany() == null || !currentUser.getCompany().getId().equals(id))) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
        }
        
        return companyService.findById(id)
                .map(company -> ResponseEntity.ok(ResponseMessage.success(company)))
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "企业不存在")));
    }

    @PostMapping
    @Operation(summary = "创建新企业（仅管理员）")
    public ResponseEntity<ResponseMessage<Company>> createCompany(@Valid @RequestBody Company company, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        if (currentUser.getRole() != User.UserRole.ADMIN) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
        }
        
        Company savedCompany = companyService.save(company);
        return ResponseEntity.ok(ResponseMessage.success("企业创建成功", savedCompany));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新企业信息（管理员可更新所有，企业用户只能更新自己的）")
    public ResponseEntity<ResponseMessage<Company>> updateCompany(@PathVariable Long id, @Valid @RequestBody Company company, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        // 企业用户只能更新自己的企业
        if (currentUser.getRole() == User.UserRole.COMPANY && 
            (currentUser.getCompany() == null || !currentUser.getCompany().getId().equals(id))) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
        }
        
        Company updatedCompany = companyService.update(id, company);
        return ResponseEntity.ok(ResponseMessage.success("企业更新成功", updatedCompany));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除企业（仅管理员）")
    public ResponseEntity<ResponseMessage<Void>> deleteCompany(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        if (currentUser.getRole() != User.UserRole.ADMIN) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
        }
        
        companyService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("企业删除成功", null));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索企业")
    public ResponseEntity<ResponseMessage<List<Company>>> searchCompanies(@RequestParam String keyword) {
        return ResponseEntity.ok(ResponseMessage.success(companyService.searchByKeyword(keyword)));
    }
}