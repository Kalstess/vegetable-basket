package com.example.cailanzi.service;

import com.example.cailanzi.dto.RoutePointDTO;
import com.example.cailanzi.entity.RoutePoint;
import com.example.cailanzi.entity.Vehicle;
import com.example.cailanzi.repository.RoutePointRepository;
import com.example.cailanzi.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoutePointService {

    private final RoutePointRepository routePointRepository;
    private final VehicleRepository vehicleRepository;

    public List<RoutePointDTO> findAll() {
        return routePointRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<RoutePointDTO> findById(Long id) {
        return routePointRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<RoutePointDTO> findByVehicleId(Long vehicleId) {
        return routePointRepository.findByVehicleId(vehicleId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RoutePointDTO> findByRouteId(String routeId) {
        return routePointRepository.findByRouteIdOrderBySeqAsc(routeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RoutePointDTO save(RoutePoint routePoint) {
        // 检查车辆是否存在
        Vehicle vehicle = vehicleRepository.findById(routePoint.getVehicle().getId())
                .orElseThrow(() -> new IllegalArgumentException("车辆不存在，ID: " + routePoint.getVehicle().getId()));
        routePoint.setVehicle(vehicle);

        // 调试日志：打印保存前的数据
        log.debug("保存路线点 - routeId: {}, seq: {}, arriveTime: {}, departTime: {}", 
                routePoint.getRouteId(), 
                routePoint.getSeq(),
                routePoint.getArriveTime(),
                routePoint.getDepartTime());

        RoutePoint saved = routePointRepository.save(routePoint);
        
        // 调试日志：打印保存后的数据
        log.debug("路线点已保存 - id: {}, departTime: {}", saved.getId(), saved.getDepartTime());
        
        return convertToDTO(saved);
    }

    public RoutePointDTO update(Long id, RoutePoint routePoint) {
        RoutePoint existing = routePointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("路线点不存在，ID: " + id));

        // 更新车辆
        if (routePoint.getVehicle() != null && routePoint.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(routePoint.getVehicle().getId())
                    .orElseThrow(() -> new IllegalArgumentException("车辆不存在，ID: " + routePoint.getVehicle().getId()));
            existing.setVehicle(vehicle);
        }

        // 更新其他字段
        if (routePoint.getRouteDate() != null) {
            existing.setRouteDate(routePoint.getRouteDate());
        }
        if (routePoint.getRouteId() != null) {
            existing.setRouteId(routePoint.getRouteId());
        }
        if (routePoint.getSeq() != null) {
            existing.setSeq(routePoint.getSeq());
        }
        if (routePoint.getAddress() != null) {
            existing.setAddress(routePoint.getAddress());
        }
        if (routePoint.getPointType() != null) {
            existing.setPointType(routePoint.getPointType());
        }
        if (routePoint.getArriveTime() != null) {
            existing.setArriveTime(routePoint.getArriveTime());
        }
        if (routePoint.getDepartTime() != null) {
            existing.setDepartTime(routePoint.getDepartTime());
        }
        if (routePoint.getDescription() != null) {
            existing.setDescription(routePoint.getDescription());
        }

        RoutePoint updated = routePointRepository.save(existing);
        return convertToDTO(updated);
    }

    public void deleteById(Long id) {
        if (!routePointRepository.existsById(id)) {
            throw new IllegalArgumentException("路线点不存在，ID: " + id);
        }
        routePointRepository.deleteById(id);
    }

    public void deleteByRouteId(String routeId) {
        List<RoutePoint> routePoints = routePointRepository.findByRouteId(routeId);
        routePointRepository.deleteAll(routePoints);
    }

    private RoutePointDTO convertToDTO(RoutePoint routePoint) {
        RoutePointDTO dto = new RoutePointDTO();
        dto.setId(routePoint.getId());
        dto.setVehicleId(routePoint.getVehicle().getId());
        dto.setVehiclePlateNumber(routePoint.getVehicle().getPlateNumber());
        dto.setRouteDate(routePoint.getRouteDate());
        dto.setRouteId(routePoint.getRouteId());
        dto.setSeq(routePoint.getSeq());
        dto.setAddress(routePoint.getAddress());
        dto.setPointType(routePoint.getPointType());
        dto.setArriveTime(routePoint.getArriveTime());
        dto.setDepartTime(routePoint.getDepartTime());
        dto.setDescription(routePoint.getDescription());
        return dto;
    }
}

