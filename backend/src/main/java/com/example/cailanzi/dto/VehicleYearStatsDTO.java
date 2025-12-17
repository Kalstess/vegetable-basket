package com.example.cailanzi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleYearStatsDTO {

    private Long vehicleId;
    private String plateNumber;
    private Long companyId;
    private String companyName;

    // 车辆类型：普通/冷藏
    private String vehicleType;

    // 年度汇总指标
    private BigDecimal yearTransportTon = BigDecimal.ZERO;
    private BigDecimal yearKilometers = BigDecimal.ZERO;
    private BigDecimal yearDeliveryCost = BigDecimal.ZERO;

    // 吨公里效率指标（可按 yearTransportTon * yearKilometers 粗略表示）
    private BigDecimal tonKilometerEfficiency = BigDecimal.ZERO;

    // 占比（相对于公司总量），小数形式
    private BigDecimal transportShare;
    private BigDecimal kilometerShare;

    // 在公司内的效率排名（1 为最高）
    private Integer efficiencyRank;
}


