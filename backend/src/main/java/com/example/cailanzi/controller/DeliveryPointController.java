package com.example.cailanzi.controller;

import com.example.cailanzi.dto.DeliveryPointDTO;
import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.entity.DeliveryPoint;
import com.example.cailanzi.service.DeliveryPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery-points")
@RequiredArgsConstructor
@Tag(name = "配送网点管理", description = "配送网点信息管理API")
public class DeliveryPointController {

    private final DeliveryPointService deliveryPointService;

    @GetMapping
    @Operation(summary = "获取所有配送网点列表")
    public ResponseEntity<ResponseMessage<List<DeliveryPointDTO>>> getAllDeliveryPoints() {
        return ResponseEntity.ok(ResponseMessage.success(deliveryPointService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取配送网点详情")
    public ResponseEntity<ResponseMessage<DeliveryPointDTO>> getDeliveryPointById(@PathVariable Long id) {
        return deliveryPointService.findById(id)
                .map(dto -> ResponseEntity.ok(ResponseMessage.success(dto)))
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "配送网点不存在")));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "根据企业ID获取配送网点列表")
    public ResponseEntity<ResponseMessage<List<DeliveryPointDTO>>> getDeliveryPointsByCompanyId(@PathVariable Long companyId) {
        return ResponseEntity.ok(ResponseMessage.success(deliveryPointService.findByCompanyId(companyId)));
    }

    @GetMapping("/company/{companyId}/active")
    @Operation(summary = "根据企业ID获取有效配送网点列表")
    public ResponseEntity<ResponseMessage<List<DeliveryPointDTO>>> getActiveDeliveryPointsByCompanyId(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "true") Boolean isActive) {
        return ResponseEntity.ok(ResponseMessage.success(deliveryPointService.findByCompanyIdAndIsActive(companyId, isActive)));
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "根据车辆ID获取配送网点列表")
    public ResponseEntity<ResponseMessage<List<DeliveryPointDTO>>> getDeliveryPointsByVehicleId(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(ResponseMessage.success(deliveryPointService.findByVehicleId(vehicleId)));
    }

    @PostMapping
    @Operation(summary = "创建配送网点")
    public ResponseEntity<ResponseMessage<DeliveryPointDTO>> createDeliveryPoint(@Valid @RequestBody DeliveryPoint deliveryPoint) {
        DeliveryPointDTO saved = deliveryPointService.save(deliveryPoint);
        return ResponseEntity.ok(ResponseMessage.success("配送网点创建成功", saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新配送网点信息")
    public ResponseEntity<ResponseMessage<DeliveryPointDTO>> updateDeliveryPoint(
            @PathVariable Long id,
            @Valid @RequestBody DeliveryPoint deliveryPoint) {
        DeliveryPointDTO updated = deliveryPointService.update(id, deliveryPoint);
        return ResponseEntity.ok(ResponseMessage.success("配送网点更新成功", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除配送网点")
    public ResponseEntity<ResponseMessage<Void>> deleteDeliveryPoint(@PathVariable Long id) {
        deliveryPointService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("配送网点删除成功", null));
    }
}

