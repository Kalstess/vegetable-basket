package com.example.cailanzi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "vehicle_statistics",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "stat_date"}))
public class VehicleStatistics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotNull(message = "统计日期不能为空")
    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    // 菜篮子工程车
    @Column(name = "regular_basket_blue")
    private Integer regularBasketBlue = 0;

    @Column(name = "regular_basket_yellow")
    private Integer regularBasketYellow = 0;

    @Column(name = "cold_basket_blue")
    private Integer coldBasketBlue = 0;

    @Column(name = "cold_basket_yellow")
    private Integer coldBasketYellow = 0;

    @Column(name = "regular_basket_green")
    private Integer regularBasketGreen = 0;

    @Column(name = "cold_basket_green")
    private Integer coldBasketGreen = 0;

    @Column(name = "basket_emission_standard")
    private Integer basketEmissionStandard = 0;

    // 非菜篮子工程车
    @Column(name = "regular_freight_blue")
    private Integer regularFreightBlue = 0;

    @Column(name = "regular_freight_yellow")
    private Integer regularFreightYellow = 0;

    @Column(name = "cold_freight_blue")
    private Integer coldFreightBlue = 0;

    @Column(name = "cold_freight_yellow")
    private Integer coldFreightYellow = 0;

    @Column(name = "freight_emission_standard")
    private Integer freightEmissionStandard = 0;

    @Column(name = "regular_freight_green")
    private Integer regularFreightGreen = 0;

    @Column(name = "cold_freight_green")
    private Integer coldFreightGreen = 0;
}
