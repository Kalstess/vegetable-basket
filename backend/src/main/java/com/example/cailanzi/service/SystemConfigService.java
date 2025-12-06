package com.example.cailanzi.service;

import com.example.cailanzi.entity.SystemConfig;
import com.example.cailanzi.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    public List<SystemConfig> findAll() {
        return systemConfigRepository.findAll();
    }

    public List<SystemConfig> findActiveConfigs() {
        return systemConfigRepository.findByIsActiveTrue();
    }

    public List<SystemConfig> findByConfigGroup(String configGroup) {
        return systemConfigRepository.findByConfigGroup(configGroup);
    }

    public Optional<SystemConfig> findById(Long id) {
        return systemConfigRepository.findById(id);
    }

    public Optional<SystemConfig> findByConfigKey(String configKey) {
        return systemConfigRepository.findByConfigKey(configKey);
    }

    public String getConfigValue(String configKey) {
        return systemConfigRepository.findByConfigKey(configKey)
                .map(SystemConfig::getConfigValue)
                .orElse(null);
    }

    public SystemConfig save(SystemConfig systemConfig) {
        if (systemConfigRepository.existsByConfigKey(systemConfig.getConfigKey())) {
            throw new IllegalArgumentException("配置键已存在: " + systemConfig.getConfigKey());
        }
        return systemConfigRepository.save(systemConfig);
    }

    public SystemConfig update(Long id, SystemConfig systemConfigDetails) {
        return systemConfigRepository.findById(id)
                .map(systemConfig -> {
                    if (!systemConfig.getConfigKey().equals(systemConfigDetails.getConfigKey()) &&
                            systemConfigRepository.existsByConfigKey(systemConfigDetails.getConfigKey())) {
                        throw new IllegalArgumentException("配置键已存在: " + systemConfigDetails.getConfigKey());
                    }

                    systemConfig.setConfigKey(systemConfigDetails.getConfigKey());
                    systemConfig.setConfigValue(systemConfigDetails.getConfigValue());
                    systemConfig.setConfigGroup(systemConfigDetails.getConfigGroup());
                    systemConfig.setDescription(systemConfigDetails.getDescription());
                    systemConfig.setIsActive(systemConfigDetails.getIsActive());

                    return systemConfigRepository.save(systemConfig);
                })
                .orElseThrow(() -> new IllegalArgumentException("系统配置不存在，ID: " + id));
    }

    public SystemConfig updateByKey(String configKey, String configValue) {
        return systemConfigRepository.findByConfigKey(configKey)
                .map(systemConfig -> {
                    systemConfig.setConfigValue(configValue);
                    return systemConfigRepository.save(systemConfig);
                })
                .orElseThrow(() -> new IllegalArgumentException("系统配置不存在，键: " + configKey));
    }

    public void deleteById(Long id) {
        if (!systemConfigRepository.existsById(id)) {
            throw new IllegalArgumentException("系统配置不存在，ID: " + id);
        }
        systemConfigRepository.deleteById(id);
    }
}
