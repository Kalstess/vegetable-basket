package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.dto.TransportStatsDTO;
import com.example.cailanzi.entity.TransportStats;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.entity.Vehicle;
import com.example.cailanzi.service.TransportService;
import com.example.cailanzi.util.JsonUtils;
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
@RequestMapping("/transport-stats")
@RequiredArgsConstructor
@Tag(name = "运输统计管理", description = "运输状况统计管理API")
public class TransportController {

    private final TransportService transportService;

    @GetMapping
    @Operation(summary = "获取运输统计列表（管理员查看所有，企业用户查看自己企业的，司机查看自己负责车辆的）")
    public ResponseEntity<ResponseMessage<List<TransportStatsDTO>>> getAllTransportStats(HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        List<TransportStatsDTO> stats;
        
        if (currentUser.getRole() == User.UserRole.ADMIN) {
            stats = transportService.findAll();
        } else if (currentUser.getRole() == User.UserRole.COMPANY && currentUser.getCompany() != null) {
            stats = transportService.findByCompanyId(currentUser.getCompany().getId());
        } else if (currentUser.getRole() == User.UserRole.DRIVER && currentUser.getVehicle() != null) {
            stats = transportService.findByVehicleId(currentUser.getVehicle().getId());
        } else {
            stats = List.of();
        }
        
        return ResponseEntity.ok(ResponseMessage.success(stats));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取运输统计详情")
    public ResponseEntity<ResponseMessage<TransportStatsDTO>> getTransportStatsById(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        return transportService.findById(id)
                .map(dto -> {
                    // 权限检查：通过 vehicleId 查找车辆，然后检查企业
                    if (currentUser.getRole() == User.UserRole.COMPANY) {
                        if (currentUser.getCompany() == null) {
                            return ResponseEntity.status(403).body(ResponseMessage.<TransportStatsDTO>error(403, "无权限访问"));
                        }
                        // 检查该记录的车辆是否属于该企业
                        List<TransportStatsDTO> companyStats = transportService.findByCompanyId(currentUser.getCompany().getId());
                        boolean hasAccess = companyStats.stream().anyMatch(stat -> stat.getId().equals(id));
                        if (!hasAccess) {
                            return ResponseEntity.status(403).body(ResponseMessage.<TransportStatsDTO>error(403, "无权限访问"));
                        }
                    }
                    if (currentUser.getRole() == User.UserRole.DRIVER && 
                        (currentUser.getVehicle() == null || !dto.getVehicleId().equals(currentUser.getVehicle().getId()))) {
                        return ResponseEntity.status(403).body(ResponseMessage.<TransportStatsDTO>error(403, "无权限访问"));
                    }
                    return ResponseEntity.ok(ResponseMessage.success(dto));
                })
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "运输统计记录不存在")));
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "根据车辆ID获取运输统计列表")
    public ResponseEntity<ResponseMessage<List<TransportStatsDTO>>> getTransportStatsByVehicleId(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(ResponseMessage.success(transportService.findByVehicleId(vehicleId)));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "根据企业ID获取运输统计列表")
    public ResponseEntity<ResponseMessage<List<TransportStatsDTO>>> getTransportStatsByCompanyId(@PathVariable Long companyId, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        // 企业用户只能查看自己企业的数据
        if (currentUser.getRole() == User.UserRole.COMPANY && 
            (currentUser.getCompany() == null || !currentUser.getCompany().getId().equals(companyId))) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
        }
        
        return ResponseEntity.ok(ResponseMessage.success(transportService.findByCompanyId(companyId)));
    }

    @PostMapping
    @Operation(summary = "创建运输统计记录")
    public ResponseEntity<ResponseMessage<TransportStatsDTO>> createTransportStats(@Valid @RequestBody TransportStatsDTO transportStatsDTO) {
        TransportStats transportStats = convertDTOToEntity(transportStatsDTO);
        TransportStatsDTO saved = transportService.save(transportStats);
        return ResponseEntity.ok(ResponseMessage.success("运输统计记录创建成功", saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新运输统计记录")
    public ResponseEntity<ResponseMessage<TransportStatsDTO>> updateTransportStats(
            @PathVariable Long id,
            @Valid @RequestBody TransportStatsDTO transportStatsDTO) {
        TransportStats transportStats = convertDTOToEntity(transportStatsDTO);
        TransportStatsDTO updated = transportService.update(id, transportStats);
        return ResponseEntity.ok(ResponseMessage.success("运输统计记录更新成功", updated));
    }

    private TransportStats convertDTOToEntity(TransportStatsDTO dto) {
        TransportStats transportStats = new TransportStats();
        if (dto.getId() != null) {
            transportStats.setId(dto.getId());
        }
        // 创建Vehicle对象，只设置id，Service层会查找完整的Vehicle对象
        Vehicle vehicle = new Vehicle();
        vehicle.setId(dto.getVehicleId());
        transportStats.setVehicle(vehicle);
        transportStats.setStatYear(dto.getStatYear());
        transportStats.setStatMonth(dto.getStatMonth());
        transportStats.setDailyDeliveryTimes(dto.getDailyDeliveryTimes());
        transportStats.setMonthProductTon(dto.getMonthProductTon());
        transportStats.setMonthKilometers(dto.getMonthKilometers());
        transportStats.setMonthDeliveryCost(dto.getMonthDeliveryCost());
        transportStats.setDailyDeliveryPoints(dto.getDailyDeliveryPoints());
        transportStats.setPeakSeasonDailyIncreaseTimes(dto.getPeakSeasonDailyIncreaseTimes());
        // productTypes转换为JSON字符串
        if (dto.getProductTypes() != null) {
            transportStats.setProductTypes(JsonUtils.toJson(dto.getProductTypes()));
        }
        return transportStats;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除运输统计记录")
    public ResponseEntity<ResponseMessage<Void>> deleteTransportStats(@PathVariable Long id) {
        transportService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("运输统计记录删除成功", null));
    }
}
