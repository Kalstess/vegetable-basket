package com.example.cailanzi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompanyTransportTrendDTO {

    private Long companyId;
    private String companyName;

    private BigDecimal transport2022;
    private BigDecimal transport2023;
    private BigDecimal transport2024;
    private BigDecimal transport2025;

    private BigDecimal revenue2025;

    // 年度同比（百分比，小数形式，如 0.15 表示 15%）
    private BigDecimal yoy2023;
    private BigDecimal yoy2024;
    private BigDecimal yoy2025;

    // 复合年均增长率 CAGR（2022-2025）
    private BigDecimal cagr;

    // 趋势类型：持续增长 / 持续下降 / 波动型
    private String trendType;
}


