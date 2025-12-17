package com.example.cailanzi.service;

import com.example.cailanzi.entity.AuditLog;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 审计日志服务，提供简易的关键操作记录能力。
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(User operator, String targetType, Long targetId, String action, String detail) {
        try {
            AuditLog logEntity = new AuditLog();
            if (operator != null) {
                logEntity.setOperatorUsername(operator.getUsername());
                logEntity.setOperatorRole(operator.getRole() != null ? operator.getRole().name() : null);
            }
            logEntity.setTargetType(targetType);
            logEntity.setTargetId(targetId);
            logEntity.setAction(action);
            logEntity.setDetail(detail);
            auditLogRepository.save(logEntity);
        } catch (Exception e) {
            // 审计失败不影响主业务，但打印日志便于排查
            log.error("Failed to write audit log", e);
        }
    }
}


