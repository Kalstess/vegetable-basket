package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ComplianceDTO;
import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.entity.Compliance;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.service.ComplianceService;
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
@RequestMapping("/compliance")
@RequiredArgsConstructor
@Tag(name = "合规运营管理", description = "合规运营记录管理API")
public class ComplianceController {

    private final ComplianceService complianceService;

    @GetMapping
    @Operation(summary = "获取合规记录列表（管理员查看所有，企业用户查看自己企业的，司机查看自己负责车辆的）")
    public ResponseEntity<ResponseMessage<List<ComplianceDTO>>> getAllCompliance(HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        List<ComplianceDTO> compliance;
        
        if (currentUser.getRole() == User.UserRole.ADMIN) {
            compliance = complianceService.findAll();
        } else if (currentUser.getRole() == User.UserRole.COMPANY && currentUser.getCompany() != null) {
            compliance = complianceService.findByCompanyId(currentUser.getCompany().getId());
        } else if (currentUser.getRole() == User.UserRole.DRIVER && currentUser.getVehicle() != null) {
            compliance = complianceService.findByVehicleId(currentUser.getVehicle().getId());
        } else {
            compliance = List.of();
        }
        
        return ResponseEntity.ok(ResponseMessage.success(compliance));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取合规记录详情")
    public ResponseEntity<ResponseMessage<ComplianceDTO>> getComplianceById(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        return complianceService.findById(id)
                .map(dto -> {
                    // 权限检查：通过 vehicleId 查找车辆，然后检查企业
                    if (currentUser.getRole() == User.UserRole.COMPANY) {
                        if (currentUser.getCompany() == null) {
                            return ResponseEntity.status(403).body(ResponseMessage.<ComplianceDTO>error(403, "无权限访问"));
                        }
                        // 检查该记录的车辆是否属于该企业
                        List<ComplianceDTO> companyCompliance = complianceService.findByCompanyId(currentUser.getCompany().getId());
                        boolean hasAccess = companyCompliance.stream().anyMatch(comp -> comp.getId().equals(id));
                        if (!hasAccess) {
                            return ResponseEntity.status(403).body(ResponseMessage.<ComplianceDTO>error(403, "无权限访问"));
                        }
                    }
                    if (currentUser.getRole() == User.UserRole.DRIVER && 
                        (currentUser.getVehicle() == null || !dto.getVehicleId().equals(currentUser.getVehicle().getId()))) {
                        return ResponseEntity.status(403).body(ResponseMessage.<ComplianceDTO>error(403, "无权限访问"));
                    }
                    return ResponseEntity.ok(ResponseMessage.success(dto));
                })
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "合规记录不存在")));
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "根据车辆ID获取合规记录列表")
    public ResponseEntity<ResponseMessage<List<ComplianceDTO>>> getComplianceByVehicleId(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(ResponseMessage.success(complianceService.findByVehicleId(vehicleId)));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "根据企业ID获取合规记录列表")
    public ResponseEntity<ResponseMessage<List<ComplianceDTO>>> getComplianceByCompanyId(@PathVariable Long companyId, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        // 企业用户只能查看自己企业的数据
        if (currentUser.getRole() == User.UserRole.COMPANY && 
            (currentUser.getCompany() == null || !currentUser.getCompany().getId().equals(companyId))) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
        }
        
        return ResponseEntity.ok(ResponseMessage.success(complianceService.findByCompanyId(companyId)));
    }

    @PostMapping
    @Operation(summary = "创建合规记录")
    public ResponseEntity<ResponseMessage<ComplianceDTO>> createCompliance(@Valid @RequestBody Compliance compliance) {
        ComplianceDTO saved = complianceService.save(compliance);
        return ResponseEntity.ok(ResponseMessage.success("合规记录创建成功", saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新合规记录")
    public ResponseEntity<ResponseMessage<ComplianceDTO>> updateCompliance(
            @PathVariable Long id,
            @Valid @RequestBody Compliance compliance) {
        ComplianceDTO updated = complianceService.update(id, compliance);
        return ResponseEntity.ok(ResponseMessage.success("合规记录更新成功", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除合规记录")
    public ResponseEntity<ResponseMessage<Void>> deleteCompliance(@PathVariable Long id) {
        complianceService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("合规记录删除成功", null));
    }
}
