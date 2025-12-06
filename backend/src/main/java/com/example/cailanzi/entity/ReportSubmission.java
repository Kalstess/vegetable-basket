package com.example.cailanzi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "report_submission")
public class ReportSubmission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotBlank(message = "提交人不能为空")
    @Column(name = "submitted_by", nullable = false)
    private String submittedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_platform")
    private SourcePlatform sourcePlatform = SourcePlatform.微信小程序;

    @Enumerated(EnumType.STRING)
    @Column(name = "submit_type")
    private SubmitType submitType = SubmitType.运输统计;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "submit_time")
    private LocalDateTime submitTime = LocalDateTime.now();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload", columnDefinition = "json")
    private String rawPayload;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    public enum SourcePlatform {
        微信小程序, 后台导入, 管理员录入
    }

    public enum SubmitType {
        企业信息, 车辆信息, 运输统计, 维护记录, 合规记录, 反馈信息
    }
}
