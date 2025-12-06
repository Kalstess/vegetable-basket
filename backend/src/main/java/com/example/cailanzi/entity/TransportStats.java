package com.example.cailanzi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "transport_stats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"vehicle_id", "stat_year", "stat_month"}))
public class TransportStats extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "stat_year", nullable = false)
    private Integer statYear;

    @Column(name = "stat_month", nullable = false)
    private Integer statMonth;

    @Column(name = "daily_delivery_times", precision = 5, scale = 2)
    private BigDecimal dailyDeliveryTimes = BigDecimal.ZERO;

    @Column(name = "month_product_ton", precision = 10, scale = 2)
    private BigDecimal monthProductTon = BigDecimal.ZERO;

    @Column(name = "month_kilometers", precision = 12, scale = 2)
    private BigDecimal monthKilometers = BigDecimal.ZERO;

    @Column(name = "month_delivery_cost", precision = 12, scale = 2)
    private BigDecimal monthDeliveryCost;

    @Column(name = "daily_delivery_points")
    private Integer dailyDeliveryPoints;

    @Column(name = "peak_season_daily_increase_times")
    private Integer peakSeasonDailyIncreaseTimes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "product_types", columnDefinition = "json")
    private String productTypes;
}