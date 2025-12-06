package com.example.cailanzi.service;

import com.example.cailanzi.dto.VehicleStatisticsDTO;
import com.example.cailanzi.entity.Company;
import com.example.cailanzi.entity.Vehicle;
import com.example.cailanzi.entity.VehicleStatistics;
import com.example.cailanzi.repository.CompanyRepository;
import com.example.cailanzi.repository.VehicleRepository;
import com.example.cailanzi.repository.VehicleStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VehicleStatisticsService {

    private final VehicleStatisticsRepository vehicleStatisticsRepository;
    private final CompanyRepository companyRepository;
    private final VehicleRepository vehicleRepository;

    public List<VehicleStatisticsDTO> findAll() {
        return vehicleStatisticsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<VehicleStatisticsDTO> findById(Long id) {
        return vehicleStatisticsRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<VehicleStatisticsDTO> findByCompanyId(Long companyId) {
        return vehicleStatisticsRepository.findByCompanyId(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<VehicleStatisticsDTO> findByCompanyIdAndStatDate(Long companyId, LocalDate statDate) {
        return vehicleStatisticsRepository.findByCompanyIdAndStatDate(companyId, statDate)
                .map(this::convertToDTO);
    }

    public VehicleStatisticsDTO save(VehicleStatistics vehicleStatistics) {
        // 检查企业是否存在
        Company company = companyRepository.findById(vehicleStatistics.getCompany().getId())
                .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + vehicleStatistics.getCompany().getId()));
        vehicleStatistics.setCompany(company);

        VehicleStatistics saved = vehicleStatisticsRepository.save(vehicleStatistics);
        return convertToDTO(saved);
    }

    public VehicleStatisticsDTO update(Long id, VehicleStatistics vehicleStatistics) {
        VehicleStatistics existing = vehicleStatisticsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("车辆统计不存在，ID: " + id));

        // 更新企业
        if (vehicleStatistics.getCompany() != null && vehicleStatistics.getCompany().getId() != null) {
            Company company = companyRepository.findById(vehicleStatistics.getCompany().getId())
                    .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + vehicleStatistics.getCompany().getId()));
            existing.setCompany(company);
        }

        // 更新统计日期
        if (vehicleStatistics.getStatDate() != null) {
            existing.setStatDate(vehicleStatistics.getStatDate());
        }

        // 更新统计数据
        if (vehicleStatistics.getRegularBasketBlue() != null) {
            existing.setRegularBasketBlue(vehicleStatistics.getRegularBasketBlue());
        }
        if (vehicleStatistics.getRegularBasketYellow() != null) {
            existing.setRegularBasketYellow(vehicleStatistics.getRegularBasketYellow());
        }
        if (vehicleStatistics.getColdBasketBlue() != null) {
            existing.setColdBasketBlue(vehicleStatistics.getColdBasketBlue());
        }
        if (vehicleStatistics.getColdBasketYellow() != null) {
            existing.setColdBasketYellow(vehicleStatistics.getColdBasketYellow());
        }
        if (vehicleStatistics.getBasketEmissionStandard() != null) {
            existing.setBasketEmissionStandard(vehicleStatistics.getBasketEmissionStandard());
        }
        if (vehicleStatistics.getRegularFreightBlue() != null) {
            existing.setRegularFreightBlue(vehicleStatistics.getRegularFreightBlue());
        }
        if (vehicleStatistics.getRegularFreightYellow() != null) {
            existing.setRegularFreightYellow(vehicleStatistics.getRegularFreightYellow());
        }
        if (vehicleStatistics.getColdFreightBlue() != null) {
            existing.setColdFreightBlue(vehicleStatistics.getColdFreightBlue());
        }
        if (vehicleStatistics.getColdFreightYellow() != null) {
            existing.setColdFreightYellow(vehicleStatistics.getColdFreightYellow());
        }
        if (vehicleStatistics.getFreightEmissionStandard() != null) {
            existing.setFreightEmissionStandard(vehicleStatistics.getFreightEmissionStandard());
        }

        VehicleStatistics updated = vehicleStatisticsRepository.save(existing);
        return convertToDTO(updated);
    }

    public void deleteById(Long id) {
        if (!vehicleStatisticsRepository.existsById(id)) {
            throw new IllegalArgumentException("车辆统计不存在，ID: " + id);
        }
        vehicleStatisticsRepository.deleteById(id);
    }

    /**
     * 根据企业车辆自动生成统计数据
     */
    public VehicleStatisticsDTO generateStatistics(Long companyId, LocalDate statDate) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + companyId));

        // 查找该企业的所有有效车辆
        List<Vehicle> vehicles = vehicleRepository.findByCompanyIdAndIsActive(companyId, true);

        VehicleStatistics statistics = new VehicleStatistics();
        statistics.setCompany(company);
        statistics.setStatDate(statDate);

        // 统计各类车辆数量
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleCategory() == Vehicle.VehicleCategory.菜篮子工程车) {
                if (vehicle.getVehicleType() == Vehicle.VehicleType.普通) {
                    if (vehicle.getColorPlate() == Vehicle.ColorPlate.蓝牌) {
                        statistics.setRegularBasketBlue(statistics.getRegularBasketBlue() + 1);
                    } else if (vehicle.getColorPlate() == Vehicle.ColorPlate.黄牌) {
                        statistics.setRegularBasketYellow(statistics.getRegularBasketYellow() + 1);
                    }
                } else if (vehicle.getVehicleType() == Vehicle.VehicleType.冷藏) {
                    if (vehicle.getColorPlate() == Vehicle.ColorPlate.蓝牌) {
                        statistics.setColdBasketBlue(statistics.getColdBasketBlue() + 1);
                    } else if (vehicle.getColorPlate() == Vehicle.ColorPlate.黄牌) {
                        statistics.setColdBasketYellow(statistics.getColdBasketYellow() + 1);
                    }
                }
                // 检查排放标准（国五以上）
                if (vehicle.getEmissionStandard() != null && 
                    (vehicle.getEmissionStandard().contains("国五") || 
                     vehicle.getEmissionStandard().contains("国六") ||
                     vehicle.getEmissionStandard().contains("国V") ||
                     vehicle.getEmissionStandard().contains("国VI"))) {
                    statistics.setBasketEmissionStandard(statistics.getBasketEmissionStandard() + 1);
                }
            } else if (vehicle.getVehicleCategory() == Vehicle.VehicleCategory.非菜篮子工程车) {
                if (vehicle.getVehicleType() == Vehicle.VehicleType.普通) {
                    if (vehicle.getColorPlate() == Vehicle.ColorPlate.蓝牌) {
                        statistics.setRegularFreightBlue(statistics.getRegularFreightBlue() + 1);
                    } else if (vehicle.getColorPlate() == Vehicle.ColorPlate.黄牌) {
                        statistics.setRegularFreightYellow(statistics.getRegularFreightYellow() + 1);
                    }
                } else if (vehicle.getVehicleType() == Vehicle.VehicleType.冷藏) {
                    if (vehicle.getColorPlate() == Vehicle.ColorPlate.蓝牌) {
                        statistics.setColdFreightBlue(statistics.getColdFreightBlue() + 1);
                    } else if (vehicle.getColorPlate() == Vehicle.ColorPlate.黄牌) {
                        statistics.setColdFreightYellow(statistics.getColdFreightYellow() + 1);
                    }
                }
                // 检查排放标准（国五以上）
                if (vehicle.getEmissionStandard() != null && 
                    (vehicle.getEmissionStandard().contains("国五") || 
                     vehicle.getEmissionStandard().contains("国六") ||
                     vehicle.getEmissionStandard().contains("国V") ||
                     vehicle.getEmissionStandard().contains("国VI"))) {
                    statistics.setFreightEmissionStandard(statistics.getFreightEmissionStandard() + 1);
                }
            }
        }

        // 检查是否已存在该日期的统计
        Optional<VehicleStatistics> existing = vehicleStatisticsRepository.findByCompanyIdAndStatDate(companyId, statDate);
        if (existing.isPresent()) {
            VehicleStatistics existingStats = existing.get();
            existingStats.setRegularBasketBlue(statistics.getRegularBasketBlue());
            existingStats.setRegularBasketYellow(statistics.getRegularBasketYellow());
            existingStats.setColdBasketBlue(statistics.getColdBasketBlue());
            existingStats.setColdBasketYellow(statistics.getColdBasketYellow());
            existingStats.setBasketEmissionStandard(statistics.getBasketEmissionStandard());
            existingStats.setRegularFreightBlue(statistics.getRegularFreightBlue());
            existingStats.setRegularFreightYellow(statistics.getRegularFreightYellow());
            existingStats.setColdFreightBlue(statistics.getColdFreightBlue());
            existingStats.setColdFreightYellow(statistics.getColdFreightYellow());
            existingStats.setFreightEmissionStandard(statistics.getFreightEmissionStandard());
            VehicleStatistics updated = vehicleStatisticsRepository.save(existingStats);
            return convertToDTO(updated);
        } else {
            VehicleStatistics saved = vehicleStatisticsRepository.save(statistics);
            return convertToDTO(saved);
        }
    }

    private VehicleStatisticsDTO convertToDTO(VehicleStatistics vehicleStatistics) {
        VehicleStatisticsDTO dto = new VehicleStatisticsDTO();
        dto.setId(vehicleStatistics.getId());
        dto.setCompanyId(vehicleStatistics.getCompany().getId());
        dto.setCompanyName(vehicleStatistics.getCompany().getName());
        dto.setStatDate(vehicleStatistics.getStatDate());
        dto.setRegularBasketBlue(vehicleStatistics.getRegularBasketBlue());
        dto.setRegularBasketYellow(vehicleStatistics.getRegularBasketYellow());
        dto.setColdBasketBlue(vehicleStatistics.getColdBasketBlue());
        dto.setColdBasketYellow(vehicleStatistics.getColdBasketYellow());
        dto.setBasketEmissionStandard(vehicleStatistics.getBasketEmissionStandard());
        dto.setRegularFreightBlue(vehicleStatistics.getRegularFreightBlue());
        dto.setRegularFreightYellow(vehicleStatistics.getRegularFreightYellow());
        dto.setColdFreightBlue(vehicleStatistics.getColdFreightBlue());
        dto.setColdFreightYellow(vehicleStatistics.getColdFreightYellow());
        dto.setFreightEmissionStandard(vehicleStatistics.getFreightEmissionStandard());
        return dto;
    }
}

