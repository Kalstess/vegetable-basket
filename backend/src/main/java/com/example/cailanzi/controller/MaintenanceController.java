package com.example.cailanzi.controller;

import com.example.cailanzi.dto.MaintenanceDTO;
import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.entity.Maintenance;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.service.MaintenanceService;
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
@RequestMapping("/maintenance")
@RequiredArgsConstructor
@Tag(name = "维护保养管理", description = "车辆维护保养记录管理API")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @GetMapping
    @Operation(summary = "获取维护记录列表（管理员查看所有，企业用户查看自己企业的，司机查看自己负责车辆的）")
    public ResponseEntity<ResponseMessage<List<MaintenanceDTO>>> getAllMaintenance(HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        List<MaintenanceDTO> maintenance;
        
        if (currentUser.getRole() == User.UserRole.ADMIN) {
            maintenance = maintenanceService.findAll();
        } else if (currentUser.getRole() == User.UserRole.COMPANY && currentUser.getCompany() != null) {
            maintenance = maintenanceService.findByCompanyId(currentUser.getCompany().getId());
        } else if (currentUser.getRole() == User.UserRole.DRIVER && currentUser.getVehicle() != null) {
            maintenance = maintenanceService.findByVehicleId(currentUser.getVehicle().getId());
        } else {
            maintenance = List.of();
        }
        
        return ResponseEntity.ok(ResponseMessage.success(maintenance));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取维护记录详情")
    public ResponseEntity<ResponseMessage<MaintenanceDTO>> getMaintenanceById(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        return maintenanceService.findById(id)
                .map(dto -> {
                    // 权限检查：通过 vehicleId 查找车辆，然后检查企业
                    if (currentUser.getRole() == User.UserRole.COMPANY) {
                        if (currentUser.getCompany() == null) {
                            return ResponseEntity.status(403).body(ResponseMessage.<MaintenanceDTO>error(403, "无权限访问"));
                        }
                        // 检查该记录的车辆是否属于该企业
                        List<MaintenanceDTO> companyMaintenance = maintenanceService.findByCompanyId(currentUser.getCompany().getId());
                        boolean hasAccess = companyMaintenance.stream().anyMatch(maint -> maint.getId().equals(id));
                        if (!hasAccess) {
                            return ResponseEntity.status(403).body(ResponseMessage.<MaintenanceDTO>error(403, "无权限访问"));
                        }
                    }
                    if (currentUser.getRole() == User.UserRole.DRIVER && 
                        (currentUser.getVehicle() == null || !dto.getVehicleId().equals(currentUser.getVehicle().getId()))) {
                        return ResponseEntity.status(403).body(ResponseMessage.<MaintenanceDTO>error(403, "无权限访问"));
                    }
                    return ResponseEntity.ok(ResponseMessage.success(dto));
                })
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "维护记录不存在")));
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "根据车辆ID获取维护记录列表")
    public ResponseEntity<ResponseMessage<List<MaintenanceDTO>>> getMaintenanceByVehicleId(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(ResponseMessage.success(maintenanceService.findByVehicleId(vehicleId)));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "根据企业ID获取维护记录列表")
    public ResponseEntity<ResponseMessage<List<MaintenanceDTO>>> getMaintenanceByCompanyId(@PathVariable Long companyId, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        // 企业用户只能查看自己企业的数据
        if (currentUser.getRole() == User.UserRole.COMPANY && 
            (currentUser.getCompany() == null || !currentUser.getCompany().getId().equals(companyId))) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
        }
        
        return ResponseEntity.ok(ResponseMessage.success(maintenanceService.findByCompanyId(companyId)));
    }

    @PostMapping
    @Operation(summary = "创建维护记录")
    public ResponseEntity<ResponseMessage<MaintenanceDTO>> createMaintenance(@Valid @RequestBody Maintenance maintenance) {
        MaintenanceDTO saved = maintenanceService.save(maintenance);
        return ResponseEntity.ok(ResponseMessage.success("维护记录创建成功", saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新维护记录")
    public ResponseEntity<ResponseMessage<MaintenanceDTO>> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody Maintenance maintenance) {
        MaintenanceDTO updated = maintenanceService.update(id, maintenance);
        return ResponseEntity.ok(ResponseMessage.success("维护记录更新成功", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除维护记录")
    public ResponseEntity<ResponseMessage<Void>> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("维护记录删除成功", null));
    }
}
