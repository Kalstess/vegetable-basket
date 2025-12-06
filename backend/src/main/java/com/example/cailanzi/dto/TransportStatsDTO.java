package com.example.cailanzi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TransportStatsDTO {
    private Long id;
    private Long vehicleId;
    private String plateNumber;
    private String companyName;
    private Integer statYear;
    private Integer statMonth;
    private BigDecimal dailyDeliveryTimes;
    private BigDecimal monthProductTon;
    private BigDecimal monthKilometers;
    private BigDecimal monthDeliveryCost;
    private Integer dailyDeliveryPoints;
    private Integer peakSeasonDailyIncreaseTimes;
    private List<String> productTypes;
}
