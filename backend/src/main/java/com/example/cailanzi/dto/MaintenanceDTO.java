package com.example.cailanzi.dto;

import com.example.cailanzi.entity.Maintenance;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MaintenanceDTO {
    private Long id;
    private Long vehicleId;
    private String plateNumber;
    private String companyName;
    private LocalDate maintDate;
    private Maintenance.MaintenanceType maintType;
    private BigDecimal cost;
    private String description;
    private String serviceProvider;
    private LocalDate nextMaintDate;
}
