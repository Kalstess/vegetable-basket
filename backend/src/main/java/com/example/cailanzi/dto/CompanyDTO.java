package com.example.cailanzi.dto;

import com.example.cailanzi.entity.Company;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CompanyDTO {
    private Long id;
    private String name;
    private LocalDate establishedDate;
    private String address;
    private String legalPersonName;
    private String legalPersonPhone;
    private String freightPassContactName;
    private String freightPassContactPhone;
    private Company.CompanyType companyType;
    private BigDecimal registeredCapital;
    private BigDecimal revenue2022;
    private BigDecimal revenue2023;
    private BigDecimal revenue2024;
    private String businessScope;
    private Integer totalVehicles;
}
