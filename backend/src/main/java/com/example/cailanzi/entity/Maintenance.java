package com.example.cailanzi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "maintenance")
public class Maintenance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull(message = "维护日期不能为空")
    @Column(name = "maint_date", nullable = false)
    private LocalDate maintDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "maint_type")
    private MaintenanceType maintType;

    @Column(name = "cost", precision = 12, scale = 2)
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "service_provider")
    private String serviceProvider;

    @Column(name = "next_maint_date")
    private LocalDate nextMaintDate;

    public enum MaintenanceType {
        日常保养, 定期维护, 故障维修, 年度检测, 其他
    }
}
