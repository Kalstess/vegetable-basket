package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.dto.RoutePointDTO;
import com.example.cailanzi.entity.RoutePoint;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.service.RoutePointService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/route-points")
@RequiredArgsConstructor
@Tag(name = "车辆路线管理", description = "车辆路线信息管理API")
public class RoutePointController {

    private final RoutePointService routePointService;
    private final VehicleService vehicleService;

    @GetMapping
    @Operation(summary = "获取路线点列表（管理员查看所有，企业用户查看自己企业的，司机查看自己负责车辆的）")
    public ResponseEntity<ResponseMessage<List<RoutePointDTO>>> getAllRoutePoints(HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        List<RoutePointDTO> routePoints;
        
        if (currentUser.getRole() == User.UserRole.ADMIN) {
            routePoints = routePointService.findAll();
        } else if (currentUser.getRole() == User.UserRole.COMPANY && currentUser.getCompany() != null) {
            // 企业用户：获取该企业的所有车辆，然后获取这些车辆的路线点
            List<Long> vehicleIds = vehicleService.findByCompanyId(currentUser.getCompany().getId())
                    .stream()
                    .map(dto -> dto.getId())
                    .collect(Collectors.toList());
            routePoints = vehicleIds.stream()
                    .flatMap(vehicleId -> routePointService.findByVehicleId(vehicleId).stream())
                    .collect(Collectors.toList());
        } else if (currentUser.getRole() == User.UserRole.DRIVER && currentUser.getVehicle() != null) {
            routePoints = routePointService.findByVehicleId(currentUser.getVehicle().getId());
        } else {
            routePoints = List.of();
        }
        
        return ResponseEntity.ok(ResponseMessage.success(routePoints));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取路线点详情")
    public ResponseEntity<ResponseMessage<RoutePointDTO>> getRoutePointById(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        return routePointService.findById(id)
                .map(dto -> {
                    // 权限检查：企业用户只能查看自己企业的车辆路线
                    if (currentUser.getRole() == User.UserRole.COMPANY) {
                        if (currentUser.getCompany() == null) {
                            return ResponseEntity.status(403).body(ResponseMessage.<RoutePointDTO>error(403, "无权限访问"));
                        }
                        // 检查该路线点的车辆是否属于该企业
                        boolean hasAccess = vehicleService.findByCompanyId(currentUser.getCompany().getId())
                                .stream()
                                .anyMatch(v -> v.getId().equals(dto.getVehicleId()));
                        if (!hasAccess) {
                            return ResponseEntity.status(403).body(ResponseMessage.<RoutePointDTO>error(403, "无权限访问"));
                        }
                    }
                    // 司机用户只能查看自己负责的车辆路线
                    if (currentUser.getRole() == User.UserRole.DRIVER) {
                        if (currentUser.getVehicle() == null || !currentUser.getVehicle().getId().equals(dto.getVehicleId())) {
                            return ResponseEntity.status(403).body(ResponseMessage.<RoutePointDTO>error(403, "无权限访问"));
                        }
                    }
                    return ResponseEntity.ok(ResponseMessage.success(dto));
                })
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "路线点不存在")));
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "根据车辆ID获取路线点列表")
    public ResponseEntity<ResponseMessage<List<RoutePointDTO>>> getRoutePointsByVehicleId(@PathVariable Long vehicleId, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        // 权限检查：企业用户只能查看自己企业的车辆路线
        if (currentUser.getRole() == User.UserRole.COMPANY) {
            if (currentUser.getCompany() == null) {
                return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
            }
            // 检查该车辆是否属于该企业
            boolean hasAccess = vehicleService.findByCompanyId(currentUser.getCompany().getId())
                    .stream()
                    .anyMatch(v -> v.getId().equals(vehicleId));
            if (!hasAccess) {
                return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
            }
        }
        // 司机用户只能查看自己负责的车辆路线
        if (currentUser.getRole() == User.UserRole.DRIVER) {
            if (currentUser.getVehicle() == null || !currentUser.getVehicle().getId().equals(vehicleId)) {
                return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
            }
        }
        
        return ResponseEntity.ok(ResponseMessage.success(routePointService.findByVehicleId(vehicleId)));
    }

    @GetMapping("/route/{routeId}")
    @Operation(summary = "根据路线ID获取路线点列表（按顺序）")
    public ResponseEntity<ResponseMessage<List<RoutePointDTO>>> getRoutePointsByRouteId(@PathVariable String routeId) {
        return ResponseEntity.ok(ResponseMessage.success(routePointService.findByRouteId(routeId)));
    }

    @PostMapping
    @Operation(summary = "创建路线点")
    public ResponseEntity<ResponseMessage<RoutePointDTO>> createRoutePoint(@Valid @RequestBody RoutePoint routePoint) {
        RoutePointDTO saved = routePointService.save(routePoint);
        return ResponseEntity.ok(ResponseMessage.success("路线点创建成功", saved));
    }

    @PostMapping("/batch")
    @Operation(summary = "批量创建路线点")
    public ResponseEntity<ResponseMessage<List<RoutePointDTO>>> createRoutePointsBatch(@Valid @RequestBody List<RoutePoint> routePoints) {
        // 调试日志：打印接收到的数据
        for (RoutePoint rp : routePoints) {
            System.out.println("接收到的路线点 - routeId: " + rp.getRouteId() + 
                    ", seq: " + rp.getSeq() + 
                    ", arriveTime: " + rp.getArriveTime() + 
                    ", departTime: " + rp.getDepartTime());
        }
        
        List<RoutePointDTO> saved = routePoints.stream()
                .map(routePointService::save)
                .toList();
        return ResponseEntity.ok(ResponseMessage.success("路线点批量创建成功", saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新路线点信息")
    public ResponseEntity<ResponseMessage<RoutePointDTO>> updateRoutePoint(
            @PathVariable Long id,
            @Valid @RequestBody RoutePoint routePoint) {
        RoutePointDTO updated = routePointService.update(id, routePoint);
        return ResponseEntity.ok(ResponseMessage.success("路线点更新成功", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除路线点")
    public ResponseEntity<ResponseMessage<Void>> deleteRoutePoint(@PathVariable Long id) {
        routePointService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("路线点删除成功", null));
    }

    @DeleteMapping("/route/{routeId}")
    @Operation(summary = "根据路线ID删除所有路线点")
    public ResponseEntity<ResponseMessage<Void>> deleteRoutePointsByRouteId(@PathVariable String routeId) {
        routePointService.deleteByRouteId(routeId);
        return ResponseEntity.ok(ResponseMessage.success("路线点删除成功", null));
    }
}

