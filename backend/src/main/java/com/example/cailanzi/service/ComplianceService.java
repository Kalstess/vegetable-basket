package com.example.cailanzi.service;

import com.example.cailanzi.dto.ComplianceDTO;
import com.example.cailanzi.entity.Compliance;
import com.example.cailanzi.entity.Vehicle;
import com.example.cailanzi.repository.ComplianceRepository;
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
public class ComplianceService {

    private final ComplianceRepository complianceRepository;
    private final VehicleRepository vehicleRepository;

    public List<ComplianceDTO> findAll() {
        return complianceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ComplianceDTO> findById(Long id) {
        return complianceRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<ComplianceDTO> findByVehicleId(Long vehicleId) {
        return complianceRepository.findByVehicleId(vehicleId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ComplianceDTO> findByCompanyId(Long companyId) {
        return complianceRepository.findByCompanyId(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ComplianceDTO save(Compliance compliance) {
        // 检查车辆是否存在
        Vehicle vehicle = vehicleRepository.findById(compliance.getVehicle().getId())
                .orElseThrow(() -> new IllegalArgumentException("车辆不存在，ID: " + compliance.getVehicle().getId()));

        // 检查是否已存在相同年月的记录
        Optional<Compliance> existing = complianceRepository.findByVehicleIdAndReportYearAndReportMonth(
                vehicle.getId(), compliance.getReportYear(), compliance.getReportMonth());
        if (existing.isPresent() && !existing.get().getId().equals(compliance.getId())) {
            throw new IllegalArgumentException("该车辆在该年月已存在合规记录");
        }

        compliance.setVehicle(vehicle);
        Compliance saved = complianceRepository.save(compliance);
        return convertToDTO(saved);
    }

    public ComplianceDTO update(Long id, Compliance complianceDetails) {
        return complianceRepository.findById(id)
                .map(compliance -> {
                    // 如果修改了年月，检查是否重复
                    if (!compliance.getReportYear().equals(complianceDetails.getReportYear()) ||
                            !compliance.getReportMonth().equals(complianceDetails.getReportMonth())) {
                        Optional<Compliance> existing = complianceRepository.findByVehicleIdAndReportYearAndReportMonth(
                                compliance.getVehicle().getId(),
                                complianceDetails.getReportYear(),
                                complianceDetails.getReportMonth());
                        if (existing.isPresent() && !existing.get().getId().equals(id)) {
                            throw new IllegalArgumentException("该车辆在该年月已存在合规记录");
                        }
                    }

                    compliance.setReportYear(complianceDetails.getReportYear());
                    compliance.setReportMonth(complianceDetails.getReportMonth());
                    compliance.setIllegalModification(complianceDetails.getIllegalModification());
                    compliance.setTrafficViolations(complianceDetails.getTrafficViolations());
                    compliance.setTrafficAccidents(complianceDetails.getTrafficAccidents());
                    compliance.setViolationDetails(complianceDetails.getViolationDetails());
                    compliance.setAccidentDetails(complianceDetails.getAccidentDetails());

                    return convertToDTO(complianceRepository.save(compliance));
                })
                .orElseThrow(() -> new IllegalArgumentException("合规记录不存在，ID: " + id));
    }

    public void deleteById(Long id) {
        if (!complianceRepository.existsById(id)) {
            throw new IllegalArgumentException("合规记录不存在，ID: " + id);
        }
        complianceRepository.deleteById(id);
    }

    private ComplianceDTO convertToDTO(Compliance compliance) {
        ComplianceDTO dto = new ComplianceDTO();
        dto.setId(compliance.getId());
        dto.setVehicleId(compliance.getVehicle().getId());
        dto.setPlateNumber(compliance.getVehicle().getPlateNumber());
        dto.setCompanyName(compliance.getVehicle().getCompany().getName());
        dto.setReportYear(compliance.getReportYear());
        dto.setReportMonth(compliance.getReportMonth());
        dto.setIllegalModification(compliance.getIllegalModification());
        dto.setTrafficViolations(compliance.getTrafficViolations());
        dto.setTrafficAccidents(compliance.getTrafficAccidents());
        dto.setViolationDetails(compliance.getViolationDetails());
        dto.setAccidentDetails(compliance.getAccidentDetails());
        return dto;
    }
}
