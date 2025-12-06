package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.entity.SystemConfig;
import com.example.cailanzi.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system-config")
@RequiredArgsConstructor
@Tag(name = "系统配置管理", description = "系统配置管理API")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    @Operation(summary = "获取所有系统配置列表")
    public ResponseEntity<ResponseMessage<List<SystemConfig>>> getAllConfigs() {
        return ResponseEntity.ok(ResponseMessage.success(systemConfigService.findAll()));
    }

    @GetMapping("/active")
    @Operation(summary = "获取所有启用的系统配置列表")
    public ResponseEntity<ResponseMessage<List<SystemConfig>>> getActiveConfigs() {
        return ResponseEntity.ok(ResponseMessage.success(systemConfigService.findActiveConfigs()));
    }

    @GetMapping("/group/{configGroup}")
    @Operation(summary = "根据配置分组获取系统配置列表")
    public ResponseEntity<ResponseMessage<List<SystemConfig>>> getConfigsByGroup(@PathVariable String configGroup) {
        return ResponseEntity.ok(ResponseMessage.success(systemConfigService.findByConfigGroup(configGroup)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取系统配置详情")
    public ResponseEntity<ResponseMessage<SystemConfig>> getConfigById(@PathVariable Long id) {
        return systemConfigService.findById(id)
                .map(dto -> ResponseEntity.ok(ResponseMessage.success(dto)))
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "系统配置不存在")));
    }

    @GetMapping("/key/{configKey}")
    @Operation(summary = "根据配置键获取系统配置")
    public ResponseEntity<ResponseMessage<SystemConfig>> getConfigByKey(@PathVariable String configKey) {
        return systemConfigService.findByConfigKey(configKey)
                .map(dto -> ResponseEntity.ok(ResponseMessage.success(dto)))
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "系统配置不存在")));
    }

    @GetMapping("/value/{configKey}")
    @Operation(summary = "根据配置键获取配置值")
    public ResponseEntity<ResponseMessage<String>> getConfigValue(@PathVariable String configKey) {
        String value = systemConfigService.getConfigValue(configKey);
        if (value != null) {
            return ResponseEntity.ok(ResponseMessage.success(value));
        } else {
            return ResponseEntity.status(404).body(ResponseMessage.error(404, "系统配置不存在"));
        }
    }

    @PostMapping
    @Operation(summary = "创建系统配置")
    public ResponseEntity<ResponseMessage<SystemConfig>> createConfig(@Valid @RequestBody SystemConfig systemConfig) {
        SystemConfig saved = systemConfigService.save(systemConfig);
        return ResponseEntity.ok(ResponseMessage.success("系统配置创建成功", saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新系统配置")
    public ResponseEntity<ResponseMessage<SystemConfig>> updateConfig(
            @PathVariable Long id,
            @Valid @RequestBody SystemConfig systemConfig) {
        SystemConfig updated = systemConfigService.update(id, systemConfig);
        return ResponseEntity.ok(ResponseMessage.success("系统配置更新成功", updated));
    }

    @PutMapping("/key/{configKey}")
    @Operation(summary = "根据配置键更新配置值")
    public ResponseEntity<ResponseMessage<SystemConfig>> updateConfigByKey(
            @PathVariable String configKey,
            @RequestParam String configValue) {
        SystemConfig updated = systemConfigService.updateByKey(configKey, configValue);
        return ResponseEntity.ok(ResponseMessage.success("系统配置更新成功", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除系统配置")
    public ResponseEntity<ResponseMessage<Void>> deleteConfig(@PathVariable Long id) {
        systemConfigService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("系统配置删除成功", null));
    }
}
