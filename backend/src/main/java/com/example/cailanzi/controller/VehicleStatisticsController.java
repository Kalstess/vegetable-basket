package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.dto.VehicleStatisticsDTO;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.entity.VehicleStatistics;
import com.example.cailanzi.service.VehicleStatisticsService;
import com.example.cailanzi.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/vehicle-statistics")
@RequiredArgsConstructor
@Tag(name = "车辆统计管理", description = "车辆数量统计管理API")
public class VehicleStatisticsController {

    private final VehicleStatisticsService vehicleStatisticsService;

    @GetMapping
    @Operation(summary = "获取车辆统计列表（管理员查看所有，企业用户查看自己企业的）")
    public ResponseEntity<ResponseMessage<List<VehicleStatisticsDTO>>> getAllVehicleStatistics(HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        List<VehicleStatisticsDTO> statistics;
        
        if (currentUser.getRole() == User.UserRole.ADMIN) {
            statistics = vehicleStatisticsService.findAll();
        } else if (currentUser.getRole() == User.UserRole.COMPANY && currentUser.getCompany() != null) {
            statistics = vehicleStatisticsService.findByCompanyId(currentUser.getCompany().getId());
        } else {
            statistics = List.of();
        }
        
        return ResponseEntity.ok(ResponseMessage.success(statistics));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取车辆统计详情")
    public ResponseEntity<ResponseMessage<VehicleStatisticsDTO>> getVehicleStatisticsById(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        return vehicleStatisticsService.findById(id)
                .map(dto -> {
                    // 权限检查
                    if (currentUser.getRole() == User.UserRole.COMPANY && 
                        (currentUser.getCompany() == null || !dto.getCompanyId().equals(currentUser.getCompany().getId()))) {
                        return ResponseEntity.status(403).body(ResponseMessage.<VehicleStatisticsDTO>error(403, "无权限访问"));
                    }
                    return ResponseEntity.ok(ResponseMessage.success(dto));
                })
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "车辆统计不存在")));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "根据企业ID获取车辆统计列表")
    public ResponseEntity<ResponseMessage<List<VehicleStatisticsDTO>>> getVehicleStatisticsByCompanyId(@PathVariable Long companyId, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        // 企业用户只能查看自己企业的数据
        if (currentUser.getRole() == User.UserRole.COMPANY && 
            (currentUser.getCompany() == null || !currentUser.getCompany().getId().equals(companyId))) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
        }
        
        return ResponseEntity.ok(ResponseMessage.success(vehicleStatisticsService.findByCompanyId(companyId)));
    }

    @GetMapping("/company/{companyId}/date")
    @Operation(summary = "根据企业ID和统计日期获取车辆统计")
    public ResponseEntity<ResponseMessage<VehicleStatisticsDTO>> getVehicleStatisticsByCompanyIdAndDate(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate statDate) {
        return vehicleStatisticsService.findByCompanyIdAndStatDate(companyId, statDate)
                .map(dto -> ResponseEntity.ok(ResponseMessage.success(dto)))
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "车辆统计不存在")));
    }

    @PostMapping
    @Operation(summary = "创建车辆统计")
    public ResponseEntity<ResponseMessage<VehicleStatisticsDTO>> createVehicleStatistics(@Valid @RequestBody VehicleStatistics vehicleStatistics) {
        VehicleStatisticsDTO saved = vehicleStatisticsService.save(vehicleStatistics);
        return ResponseEntity.ok(ResponseMessage.success("车辆统计创建成功", saved));
    }

    @PostMapping("/generate")
    @Operation(summary = "根据企业车辆自动生成统计数据")
    public ResponseEntity<ResponseMessage<VehicleStatisticsDTO>> generateVehicleStatistics(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate statDate) {
        VehicleStatisticsDTO generated = vehicleStatisticsService.generateStatistics(companyId, statDate);
        return ResponseEntity.ok(ResponseMessage.success("车辆统计生成成功", generated));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新车辆统计信息")
    public ResponseEntity<ResponseMessage<VehicleStatisticsDTO>> updateVehicleStatistics(
            @PathVariable Long id,
            @Valid @RequestBody VehicleStatistics vehicleStatistics) {
        VehicleStatisticsDTO updated = vehicleStatisticsService.update(id, vehicleStatistics);
        return ResponseEntity.ok(ResponseMessage.success("车辆统计更新成功", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除车辆统计")
    public ResponseEntity<ResponseMessage<Void>> deleteVehicleStatistics(@PathVariable Long id) {
        vehicleStatisticsService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("车辆统计删除成功", null));
    }
}

