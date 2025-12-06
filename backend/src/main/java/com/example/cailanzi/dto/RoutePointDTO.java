package com.example.cailanzi.dto;

import com.example.cailanzi.entity.RoutePoint;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RoutePointDTO {
    private Long id;
    private Long vehicleId;
    private String vehiclePlateNumber;
    private LocalDate routeDate;
    private String routeId;
    private Integer seq;
    private String address;
    private RoutePoint.PointType pointType;
    private LocalDateTime arriveTime;
    private String description;
}

