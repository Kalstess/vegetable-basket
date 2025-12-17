package com.example.cailanzi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 简单审计日志实体，用于记录关键操作（账号创建、审批等）。
 */
@Getter
@Setter
@Entity
@Table(name = "audit_log")
public class AuditLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 操作人用户名（冗余保存，便于审计）
     */
    @Column(name = "operator_username", length = 50)
    private String operatorUsername;

    /**
     * 操作人角色
     */
    @Column(name = "operator_role", length = 50)
    private String operatorRole;

    /**
     * 目标类型，如 USER / COMPANY / VEHICLE / FEEDBACK 等
     */
    @Column(name = "target_type", length = 50)
    private String targetType;

    /**
     * 目标主键
     */
    @Column(name = "target_id")
    private Long targetId;

    /**
     * 动作，如 CREATE / UPDATE / DELETE / APPROVE / REJECT 等
     */
    @Column(name = "action", length = 50)
    private String action;

    /**
     * 详情 JSON 或简单文本
     */
    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;
}


