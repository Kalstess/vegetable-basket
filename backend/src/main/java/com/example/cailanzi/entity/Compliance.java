package com.example.cailanzi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "compliance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"vehicle_id", "report_year", "report_month"}))
public class Compliance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull(message = "报告年份不能为空")
    @Column(name = "report_year", nullable = false)
    private Integer reportYear;

    @NotNull(message = "报告月份不能为空")
    @Column(name = "report_month", nullable = false)
    private Integer reportMonth;

    @Column(name = "illegal_modification")
    private Boolean illegalModification = false;

    @Column(name = "traffic_violations")
    private Integer trafficViolations = 0;

    @Column(name = "traffic_accidents")
    private Integer trafficAccidents = 0;

    @Column(name = "violation_details", columnDefinition = "TEXT")
    private String violationDetails;

    @Column(name = "accident_details", columnDefinition = "TEXT")
    private String accidentDetails;
}
