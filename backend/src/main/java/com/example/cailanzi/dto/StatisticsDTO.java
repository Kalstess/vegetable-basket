package com.example.cailanzi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    private Long companyCount;
    private Long vehicleCount;
    private Long transportCount;
    private Long feedbackCount;
}

