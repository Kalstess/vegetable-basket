package com.example.cailanzi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "feedback")
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @NotNull(message = "报告年份不能为空")
    @Column(name = "report_year", nullable = false)
    private Integer reportYear;

    @NotNull(message = "报告月份不能为空")
    @Column(name = "report_month", nullable = false)
    private Integer reportMonth;

    @Column(name = "main_operational_difficulties", columnDefinition = "TEXT")
    private String mainOperationalDifficulties;

    @Column(name = "policy_suggestions", columnDefinition = "TEXT")
    private String policySuggestions;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FeedbackStatus status = FeedbackStatus.待处理;

    @Column(name = "response", columnDefinition = "TEXT")
    private String response;

    public enum FeedbackStatus {
        待处理, 处理中, 已处理, 已关闭
    }
}
