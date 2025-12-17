package com.example.cailanzi.controller;

import com.example.cailanzi.dto.CompanyTransportTrendDTO;
import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.dto.VehicleYearStatsDTO;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.service.AnalyticsService;
import com.example.cailanzi.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "高级分析", description = "企业与车辆运营分析 API")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/company/transport-trends")
    @Operation(summary = "企业 2022-2025 年运输量与营收趋势分析")
    public ResponseEntity<ResponseMessage<List<CompanyTransportTrendDTO>>> getCompanyTransportTrends(
            @RequestParam(required = false) Long companyId,
            HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }

        // 企业用户（包括企业管理员和普通用户）默认只看自己企业
        if ((currentUser.getRole() == User.UserRole.COMPANY 
                || currentUser.getRole() == User.UserRole.COMPANY_ADMIN
                || currentUser.getRole() == User.UserRole.COMPANY_USER) 
                && currentUser.getCompany() != null) {
            companyId = currentUser.getCompany().getId();
        }

        List<CompanyTransportTrendDTO> data = analyticsService.getCompanyTransportTrends(companyId);
        return ResponseEntity.ok(ResponseMessage.success(data));
    }

    @GetMapping("/company/vehicle-year-stats")
    @Operation(summary = "企业内车辆年度运营统计")
    public ResponseEntity<ResponseMessage<List<VehicleYearStatsDTO>>> getVehicleYearStats(
            @RequestParam(required = false) Long companyId,
            @RequestParam Integer year,
            HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }

        // 企业用户（包括企业管理员和普通用户）默认只看自己企业
        if ((currentUser.getRole() == User.UserRole.COMPANY 
                || currentUser.getRole() == User.UserRole.COMPANY_ADMIN
                || currentUser.getRole() == User.UserRole.COMPANY_USER) 
                && currentUser.getCompany() != null) {
            companyId = currentUser.getCompany().getId();
        }

        List<VehicleYearStatsDTO> data = analyticsService.getVehicleYearStats(companyId, year);
        return ResponseEntity.ok(ResponseMessage.success(data));
    }
}


