package com.example.cailanzi.dto;

import lombok.Data;

@Data
public class ComplianceDTO {
    private Long id;
    private Long vehicleId;
    private String plateNumber;
    private String companyName;
    private Integer reportYear;
    private Integer reportMonth;
    private Boolean illegalModification;
    private Integer trafficViolations;
    private Integer trafficAccidents;
    private String violationDetails;
    private String accidentDetails;
}
