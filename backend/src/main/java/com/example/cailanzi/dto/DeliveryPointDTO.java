package com.example.cailanzi.dto;

import com.example.cailanzi.entity.DeliveryPoint;
import lombok.Data;

@Data
public class DeliveryPointDTO {
    private Long id;
    private Long companyId;
    private String companyName;
    private Long vehicleId;
    private String vehiclePlateNumber;
    private String name;
    private String address;
    private DeliveryPoint.PointType pointType;
    private String contactPerson;
    private String contactPhone;
    private Boolean isActive;
}

