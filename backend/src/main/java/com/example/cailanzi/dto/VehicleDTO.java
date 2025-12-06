package com.example.cailanzi.dto;

import com.example.cailanzi.entity.Vehicle;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VehicleDTO {
    private Long id;
    private Long companyId;
    private String companyName;
    private String plateNumber;
    private Vehicle.VehicleCategory vehicleCategory;
    private Vehicle.VehicleType vehicleType;
    private Vehicle.ColorPlate colorPlate;
    private String emissionStandard;
    private BigDecimal approvedLoad;
    private LocalDate purchaseDate;
    private String vin;
    private String engineNo;
    private String drivingLicenseData;
    private Integer dataVersion;
    private String lastUpdatedReason;
    private Boolean isActive;
}
