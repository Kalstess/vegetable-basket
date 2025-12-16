package com.example.cailanzi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "vehicle",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "plate_number"}))
public class Vehicle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotBlank(message = "车牌号不能为空")
    @Column(name = "plate_number", nullable = false)
    private String plateNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_category")
    private VehicleCategory vehicleCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "color_plate")
    private ColorPlate colorPlate;

    @Column(name = "emission_standard")
    private String emissionStandard;

    @Column(name = "approved_load", precision = 8, scale = 2)
    private BigDecimal approvedLoad;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    private String vin;

    @Column(name = "engine_no")
    private String engineNo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "driving_license_data", columnDefinition = "json")
    private String drivingLicenseData;

    @Column(name = "data_version")
    private Integer dataVersion = 1;

    @Column(name = "last_updated_reason")
    private String lastUpdatedReason;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public enum VehicleCategory {
        菜篮子工程车, 非菜篮子工程车
    }

    public enum VehicleType {
        普通, 冷藏
    }

    public enum ColorPlate {
        蓝牌, 黄牌, 绿牌
    }
}