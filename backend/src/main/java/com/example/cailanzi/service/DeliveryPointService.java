package com.example.cailanzi.service;

import com.example.cailanzi.dto.DeliveryPointDTO;
import com.example.cailanzi.entity.Company;
import com.example.cailanzi.entity.DeliveryPoint;
import com.example.cailanzi.entity.Vehicle;
import com.example.cailanzi.repository.CompanyRepository;
import com.example.cailanzi.repository.DeliveryPointRepository;
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
public class DeliveryPointService {

    private final DeliveryPointRepository deliveryPointRepository;
    private final CompanyRepository companyRepository;
    private final VehicleRepository vehicleRepository;

    public List<DeliveryPointDTO> findAll() {
        return deliveryPointRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<DeliveryPointDTO> findById(Long id) {
        return deliveryPointRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<DeliveryPointDTO> findByCompanyId(Long companyId) {
        return deliveryPointRepository.findByCompanyId(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DeliveryPointDTO> findByCompanyIdAndIsActive(Long companyId, Boolean isActive) {
        return deliveryPointRepository.findByCompanyIdAndIsActive(companyId, isActive).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DeliveryPointDTO> findByVehicleId(Long vehicleId) {
        return deliveryPointRepository.findByVehicleId(vehicleId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DeliveryPointDTO save(DeliveryPoint deliveryPoint) {
        // 检查企业是否存在
        Company company = companyRepository.findById(deliveryPoint.getCompany().getId())
                .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + deliveryPoint.getCompany().getId()));
        deliveryPoint.setCompany(company);

        // 如果指定了车辆，检查车辆是否存在
        if (deliveryPoint.getVehicle() != null && deliveryPoint.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(deliveryPoint.getVehicle().getId())
                    .orElseThrow(() -> new IllegalArgumentException("车辆不存在，ID: " + deliveryPoint.getVehicle().getId()));
            deliveryPoint.setVehicle(vehicle);
        } else {
            deliveryPoint.setVehicle(null);
        }

        DeliveryPoint saved = deliveryPointRepository.save(deliveryPoint);
        return convertToDTO(saved);
    }

    public DeliveryPointDTO update(Long id, DeliveryPoint deliveryPoint) {
        DeliveryPoint existing = deliveryPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("配送网点不存在，ID: " + id));

        // 更新企业
        if (deliveryPoint.getCompany() != null && deliveryPoint.getCompany().getId() != null) {
            Company company = companyRepository.findById(deliveryPoint.getCompany().getId())
                    .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + deliveryPoint.getCompany().getId()));
            existing.setCompany(company);
        }

        // 更新车辆
        if (deliveryPoint.getVehicle() != null && deliveryPoint.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(deliveryPoint.getVehicle().getId())
                    .orElseThrow(() -> new IllegalArgumentException("车辆不存在，ID: " + deliveryPoint.getVehicle().getId()));
            existing.setVehicle(vehicle);
        } else {
            existing.setVehicle(null);
        }

        // 更新其他字段
        if (deliveryPoint.getName() != null) {
            existing.setName(deliveryPoint.getName());
        }
        if (deliveryPoint.getAddress() != null) {
            existing.setAddress(deliveryPoint.getAddress());
        }
        if (deliveryPoint.getPointType() != null) {
            existing.setPointType(deliveryPoint.getPointType());
        }
        if (deliveryPoint.getContactPerson() != null) {
            existing.setContactPerson(deliveryPoint.getContactPerson());
        }
        if (deliveryPoint.getContactPhone() != null) {
            existing.setContactPhone(deliveryPoint.getContactPhone());
        }
        if (deliveryPoint.getIsActive() != null) {
            existing.setIsActive(deliveryPoint.getIsActive());
        }

        DeliveryPoint updated = deliveryPointRepository.save(existing);
        return convertToDTO(updated);
    }

    public void deleteById(Long id) {
        if (!deliveryPointRepository.existsById(id)) {
            throw new IllegalArgumentException("配送网点不存在，ID: " + id);
        }
        deliveryPointRepository.deleteById(id);
    }

    private DeliveryPointDTO convertToDTO(DeliveryPoint deliveryPoint) {
        DeliveryPointDTO dto = new DeliveryPointDTO();
        dto.setId(deliveryPoint.getId());
        dto.setCompanyId(deliveryPoint.getCompany().getId());
        dto.setCompanyName(deliveryPoint.getCompany().getName());
        if (deliveryPoint.getVehicle() != null) {
            dto.setVehicleId(deliveryPoint.getVehicle().getId());
            dto.setVehiclePlateNumber(deliveryPoint.getVehicle().getPlateNumber());
        }
        dto.setName(deliveryPoint.getName());
        dto.setAddress(deliveryPoint.getAddress());
        dto.setPointType(deliveryPoint.getPointType());
        dto.setContactPerson(deliveryPoint.getContactPerson());
        dto.setContactPhone(deliveryPoint.getContactPhone());
        dto.setIsActive(deliveryPoint.getIsActive());
        return dto;
    }
}

