package com.example.cailanzi.service;

import com.example.cailanzi.dto.MaintenanceDTO;
import com.example.cailanzi.entity.Maintenance;
import com.example.cailanzi.entity.Vehicle;
import com.example.cailanzi.repository.MaintenanceRepository;
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
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    public List<MaintenanceDTO> findAll() {
        return maintenanceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<MaintenanceDTO> findById(Long id) {
        return maintenanceRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<MaintenanceDTO> findByVehicleId(Long vehicleId) {
        return maintenanceRepository.findByVehicleIdOrderByMaintDateDesc(vehicleId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MaintenanceDTO> findByCompanyId(Long companyId) {
        return maintenanceRepository.findByCompanyId(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MaintenanceDTO save(Maintenance maintenance) {
        // 检查车辆是否存在
        Vehicle vehicle = vehicleRepository.findById(maintenance.getVehicle().getId())
                .orElseThrow(() -> new IllegalArgumentException("车辆不存在，ID: " + maintenance.getVehicle().getId()));

        maintenance.setVehicle(vehicle);
        Maintenance saved = maintenanceRepository.save(maintenance);
        return convertToDTO(saved);
    }

    public MaintenanceDTO update(Long id, Maintenance maintenanceDetails) {
        return maintenanceRepository.findById(id)
                .map(maintenance -> {
                    maintenance.setMaintDate(maintenanceDetails.getMaintDate());
                    maintenance.setMaintType(maintenanceDetails.getMaintType());
                    maintenance.setCost(maintenanceDetails.getCost());
                    maintenance.setDescription(maintenanceDetails.getDescription());
                    maintenance.setServiceProvider(maintenanceDetails.getServiceProvider());
                    maintenance.setNextMaintDate(maintenanceDetails.getNextMaintDate());

                    return convertToDTO(maintenanceRepository.save(maintenance));
                })
                .orElseThrow(() -> new IllegalArgumentException("维护记录不存在，ID: " + id));
    }

    public void deleteById(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new IllegalArgumentException("维护记录不存在，ID: " + id);
        }
        maintenanceRepository.deleteById(id);
    }

    private MaintenanceDTO convertToDTO(Maintenance maintenance) {
        MaintenanceDTO dto = new MaintenanceDTO();
        dto.setId(maintenance.getId());
        dto.setVehicleId(maintenance.getVehicle().getId());
        dto.setPlateNumber(maintenance.getVehicle().getPlateNumber());
        dto.setCompanyName(maintenance.getVehicle().getCompany().getName());
        dto.setMaintDate(maintenance.getMaintDate());
        dto.setMaintType(maintenance.getMaintType());
        dto.setCost(maintenance.getCost());
        dto.setDescription(maintenance.getDescription());
        dto.setServiceProvider(maintenance.getServiceProvider());
        dto.setNextMaintDate(maintenance.getNextMaintDate());
        return dto;
    }
}
