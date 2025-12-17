package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.dto.VehicleDTO;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.service.VehicleService;
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
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Tag(name = "车辆管理", description = "车辆信息管理API")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    @Operation(summary = "获取车辆列表（管理员查看所有，企业用户查看自己企业的，司机查看自己负责的）")
    public ResponseEntity<ResponseMessage<List<VehicleDTO>>> getAllVehicles(HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        List<VehicleDTO> vehicles;
        
        if (currentUser.getRole() == User.UserRole.ADMIN 
                || currentUser.getRole() == User.UserRole.BUSINESS_COMMISSION) {
            // 管理员和商务委可以查看所有车辆
            vehicles = vehicleService.findAll();
        } else if ((currentUser.getRole() == User.UserRole.COMPANY 
                || currentUser.getRole() == User.UserRole.COMPANY_ADMIN
                || currentUser.getRole() == User.UserRole.COMPANY_USER) 
                && currentUser.getCompany() != null) {
            // 企业用户（包括企业管理员和普通用户）查看本企业的车辆
            vehicles = vehicleService.findByCompanyId(currentUser.getCompany().getId());
        } else if (currentUser.getRole() == User.UserRole.DRIVER && currentUser.getVehicle() != null) {
            vehicles = vehicleService.findById(currentUser.getVehicle().getId())
                    .map(List::of)
                    .orElse(List.of());
        } else {
            vehicles = List.of();
        }
        
        return ResponseEntity.ok(ResponseMessage.success(vehicles));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取车辆详情")
    public ResponseEntity<ResponseMessage<VehicleDTO>> getVehicleById(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        return vehicleService.findById(id)
                .map(dto -> {
                    // 权限检查
                    if (currentUser.getRole() == User.UserRole.COMPANY && 
                        (currentUser.getCompany() == null || !dto.getCompanyId().equals(currentUser.getCompany().getId()))) {
                        return ResponseEntity.status(403).body(ResponseMessage.<VehicleDTO>error(403, "无权限访问"));
                    }
                    if (currentUser.getRole() == User.UserRole.DRIVER && 
                        (currentUser.getVehicle() == null || !currentUser.getVehicle().getId().equals(id))) {
                        return ResponseEntity.status(403).body(ResponseMessage.<VehicleDTO>error(403, "无权限访问"));
                    }
                    return ResponseEntity.ok(ResponseMessage.success(dto));
                })
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "车辆不存在")));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "根据企业ID获取车辆列表")
    public ResponseEntity<ResponseMessage<List<VehicleDTO>>> getVehiclesByCompanyId(@PathVariable Long companyId, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        // 企业用户只能查看自己企业的车辆
        if (currentUser.getRole() == User.UserRole.COMPANY && 
            (currentUser.getCompany() == null || !currentUser.getCompany().getId().equals(companyId))) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
        }
        
        return ResponseEntity.ok(ResponseMessage.success(vehicleService.findByCompanyId(companyId)));
    }

    @GetMapping("/company/{companyId}/active")
    @Operation(summary = "根据企业ID获取有效车辆列表")
    public ResponseEntity<ResponseMessage<List<VehicleDTO>>> getActiveVehiclesByCompanyId(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "true") Boolean isActive) {
        return ResponseEntity.ok(ResponseMessage.success(vehicleService.findByCompanyIdAndIsActive(companyId, isActive)));
    }

    @PostMapping
    @Operation(summary = "创建新车辆")
    public ResponseEntity<ResponseMessage<VehicleDTO>> createVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        VehicleDTO saved = vehicleService.save(vehicleDTO);
        return ResponseEntity.ok(ResponseMessage.success("车辆创建成功", saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新车辆信息")
    public ResponseEntity<ResponseMessage<VehicleDTO>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDTO vehicleDTO) {
        VehicleDTO updated = vehicleService.update(id, vehicleDTO);
        return ResponseEntity.ok(ResponseMessage.success("车辆更新成功", updated));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新车辆状态")
    public ResponseEntity<ResponseMessage<Void>> updateVehicleStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {
        vehicleService.updateStatus(id, isActive);
        return ResponseEntity.ok(ResponseMessage.success("车辆状态更新成功", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除车辆")
    public ResponseEntity<ResponseMessage<Void>> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("车辆删除成功", null));
    }
}
