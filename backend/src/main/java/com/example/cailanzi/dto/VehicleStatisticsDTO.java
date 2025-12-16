package com.example.cailanzi.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VehicleStatisticsDTO {
    private Long id;
    private Long companyId;
    private String companyName;
    private LocalDate statDate;
    
    // 菜篮子工程车
    private Integer regularBasketBlue = 0;
    private Integer regularBasketYellow = 0;
    private Integer regularBasketGreen = 0;
    private Integer coldBasketBlue = 0;
    private Integer coldBasketYellow = 0;
    private Integer coldBasketGreen = 0;
    private Integer basketEmissionStandard = 0;
    
    // 非菜篮子工程车
    private Integer regularFreightBlue = 0;
    private Integer regularFreightYellow = 0;
    private Integer regularFreightGreen = 0;
    private Integer coldFreightBlue = 0;
    private Integer coldFreightYellow = 0;
    private Integer coldFreightGreen = 0;
    private Integer freightEmissionStandard = 0;
}

