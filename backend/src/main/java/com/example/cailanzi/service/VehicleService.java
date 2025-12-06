package com.example.cailanzi.service;

import com.example.cailanzi.dto.VehicleDTO;
import com.example.cailanzi.entity.Company;
import com.example.cailanzi.entity.Vehicle;
import com.example.cailanzi.repository.CompanyRepository;
import com.example.cailanzi.repository.VehicleRepository;
import org.springframework.util.StringUtils;
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
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CompanyRepository companyRepository;

    public List<VehicleDTO> findAll() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<VehicleDTO> findById(Long id) {
        return vehicleRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<VehicleDTO> findByCompanyId(Long companyId) {
        return vehicleRepository.findByCompanyId(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<VehicleDTO> findByCompanyIdAndIsActive(Long companyId, Boolean isActive) {
        return vehicleRepository.findByCompanyIdAndIsActive(companyId, isActive).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public VehicleDTO save(Vehicle vehicle) {
        // 检查企业是否存在
        Company company = companyRepository.findById(vehicle.getCompany().getId())
                .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + vehicle.getCompany().getId()));

        // 检查同一企业下是否已存在相同车牌号
        if (vehicleRepository.findByPlateNumberAndCompanyId(vehicle.getPlateNumber(), vehicle.getCompany().getId()).isPresent()) {
            throw new IllegalArgumentException("该企业下已存在车牌号: " + vehicle.getPlateNumber());
        }

        vehicle.setCompany(company);
        Vehicle saved = vehicleRepository.save(vehicle);
        return convertToDTO(saved);
    }

    public VehicleDTO save(VehicleDTO dto) {
        if (dto.getCompanyId() == null) {
            throw new IllegalArgumentException("所属企业不能为空");
        }
        if (!StringUtils.hasText(dto.getPlateNumber())) {
            throw new IllegalArgumentException("车牌号不能为空");
        }
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + dto.getCompanyId()));
        if (vehicleRepository.findByPlateNumberAndCompanyId(dto.getPlateNumber(), dto.getCompanyId()).isPresent()) {
            throw new IllegalArgumentException("该企业下已存在车牌号: " + dto.getPlateNumber());
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setCompany(company);
        vehicle.setPlateNumber(dto.getPlateNumber());
        vehicle.setVehicleCategory(dto.getVehicleCategory());
        vehicle.setVehicleType(dto.getVehicleType());
        vehicle.setColorPlate(dto.getColorPlate());
        vehicle.setEmissionStandard(dto.getEmissionStandard());
        vehicle.setApprovedLoad(dto.getApprovedLoad());
        vehicle.setPurchaseDate(dto.getPurchaseDate());
        vehicle.setVin(dto.getVin());
        vehicle.setEngineNo(dto.getEngineNo());
        vehicle.setDrivingLicenseData(dto.getDrivingLicenseData());
        vehicle.setDataVersion(dto.getDataVersion() != null ? dto.getDataVersion() : 1);
        vehicle.setLastUpdatedReason(dto.getLastUpdatedReason());
        vehicle.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return convertToDTO(vehicleRepository.save(vehicle));
    }

    public VehicleDTO update(Long id, Vehicle vehicleDetails) {
        return vehicleRepository.findById(id)
                .map(vehicle -> {
                    // 如果修改了企业，检查新企业是否存在
                    if (!vehicle.getCompany().getId().equals(vehicleDetails.getCompany().getId())) {
                        Company company = companyRepository.findById(vehicleDetails.getCompany().getId())
                                .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + vehicleDetails.getCompany().getId()));
                        vehicle.setCompany(company);
                    }

                    // 如果修改了车牌号，检查是否重复
                    if (!vehicle.getPlateNumber().equals(vehicleDetails.getPlateNumber())) {
                        if (vehicleRepository.findByPlateNumberAndCompanyId(vehicleDetails.getPlateNumber(), vehicle.getCompany().getId()).isPresent()) {
                            throw new IllegalArgumentException("该企业下已存在车牌号: " + vehicleDetails.getPlateNumber());
                        }
                    }

                    vehicle.setPlateNumber(vehicleDetails.getPlateNumber());
                    vehicle.setVehicleCategory(vehicleDetails.getVehicleCategory());
                    vehicle.setVehicleType(vehicleDetails.getVehicleType());
                    vehicle.setColorPlate(vehicleDetails.getColorPlate());
                    vehicle.setEmissionStandard(vehicleDetails.getEmissionStandard());
                    vehicle.setApprovedLoad(vehicleDetails.getApprovedLoad());
                    vehicle.setPurchaseDate(vehicleDetails.getPurchaseDate());
                    vehicle.setVin(vehicleDetails.getVin());
                    vehicle.setEngineNo(vehicleDetails.getEngineNo());
                    vehicle.setDrivingLicenseData(vehicleDetails.getDrivingLicenseData());
                    vehicle.setLastUpdatedReason(vehicleDetails.getLastUpdatedReason());
                    vehicle.setIsActive(vehicleDetails.getIsActive());

                    if (vehicleDetails.getDataVersion() != null) {
                        vehicle.setDataVersion(vehicleDetails.getDataVersion() + 1);
                    }

                    return convertToDTO(vehicleRepository.save(vehicle));
                })
                .orElseThrow(() -> new IllegalArgumentException("车辆不存在，ID: " + id));
    }

    public VehicleDTO update(Long id, VehicleDTO dto) {
        return vehicleRepository.findById(id)
                .map(vehicle -> {
                    // 企业处理
                    if (dto.getCompanyId() != null && !dto.getCompanyId().equals(vehicle.getCompany().getId())) {
                        Company company = companyRepository.findById(dto.getCompanyId())
                                .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + dto.getCompanyId()));
                        vehicle.setCompany(company);
                    }
                    // 车牌重复校验
                    if (dto.getPlateNumber() != null && !dto.getPlateNumber().equals(vehicle.getPlateNumber())) {
                        Long checkCompanyId = dto.getCompanyId() != null ? dto.getCompanyId() : vehicle.getCompany().getId();
                        if (vehicleRepository.findByPlateNumberAndCompanyId(dto.getPlateNumber(), checkCompanyId).isPresent()) {
                            throw new IllegalArgumentException("该企业下已存在车牌号: " + dto.getPlateNumber());
                        }
                        vehicle.setPlateNumber(dto.getPlateNumber());
                    }
                    // 其他字段
                    vehicle.setVehicleCategory(dto.getVehicleCategory());
                    vehicle.setVehicleType(dto.getVehicleType());
                    vehicle.setColorPlate(dto.getColorPlate());
                    vehicle.setEmissionStandard(dto.getEmissionStandard());
                    vehicle.setApprovedLoad(dto.getApprovedLoad());
                    vehicle.setPurchaseDate(dto.getPurchaseDate());
                    vehicle.setVin(dto.getVin());
                    vehicle.setEngineNo(dto.getEngineNo());
                    vehicle.setDrivingLicenseData(dto.getDrivingLicenseData());
                    vehicle.setLastUpdatedReason(dto.getLastUpdatedReason());
                    if (dto.getIsActive() != null) {
                        vehicle.setIsActive(dto.getIsActive());
                    }
                    if (dto.getDataVersion() != null) {
                        vehicle.setDataVersion(dto.getDataVersion() + 1);
                    }
                    return convertToDTO(vehicleRepository.save(vehicle));
                })
                .orElseThrow(() -> new IllegalArgumentException("车辆不存在，ID: " + id));
    }

    public void deleteById(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new IllegalArgumentException("车辆不存在，ID: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public void updateStatus(Long id, Boolean isActive) {
        vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("车辆不存在，ID: " + id));
        vehicleRepository.updateStatus(id, isActive);
    }

    private VehicleDTO convertToDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setCompanyId(vehicle.getCompany().getId());
        dto.setCompanyName(vehicle.getCompany().getName());
        dto.setPlateNumber(vehicle.getPlateNumber());
        dto.setVehicleCategory(vehicle.getVehicleCategory());
        dto.setVehicleType(vehicle.getVehicleType());
        dto.setColorPlate(vehicle.getColorPlate());
        dto.setEmissionStandard(vehicle.getEmissionStandard());
        dto.setApprovedLoad(vehicle.getApprovedLoad());
        dto.setPurchaseDate(vehicle.getPurchaseDate());
        dto.setVin(vehicle.getVin());
        dto.setEngineNo(vehicle.getEngineNo());
        dto.setDrivingLicenseData(vehicle.getDrivingLicenseData());
        dto.setDataVersion(vehicle.getDataVersion());
        dto.setLastUpdatedReason(vehicle.getLastUpdatedReason());
        dto.setIsActive(vehicle.getIsActive());
        return dto;
    }
}
