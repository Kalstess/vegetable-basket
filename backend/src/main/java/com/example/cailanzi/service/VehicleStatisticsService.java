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
        if (vehicleStatistics.getRegularBasketGreen() != null) {
            existing.setRegularBasketGreen(vehicleStatistics.getRegularBasketGreen());
        }
        if (vehicleStatistics.getColdBasketGreen() != null) {
            existing.setColdBasketGreen(vehicleStatistics.getColdBasketGreen());
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
        if (vehicleStatistics.getRegularFreightGreen() != null) {
            existing.setRegularFreightGreen(vehicleStatistics.getRegularFreightGreen());
        }
        if (vehicleStatistics.getColdFreightGreen() != null) {
            existing.setColdFreightGreen(vehicleStatistics.getColdFreightGreen());
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
                        } else if (vehicle.getColorPlate() == Vehicle.ColorPlate.绿牌) {
                            statistics.setRegularBasketGreen(statistics.getRegularBasketGreen() + 1);
                        }
                    } else if (vehicle.getVehicleType() == Vehicle.VehicleType.冷藏) {
                        if (vehicle.getColorPlate() == Vehicle.ColorPlate.蓝牌) {
                            statistics.setColdBasketBlue(statistics.getColdBasketBlue() + 1);
                        } else if (vehicle.getColorPlate() == Vehicle.ColorPlate.黄牌) {
                            statistics.setColdBasketYellow(statistics.getColdBasketYellow() + 1);
                        } else if (vehicle.getColorPlate() == Vehicle.ColorPlate.绿牌) {
                            statistics.setColdBasketGreen(statistics.getColdBasketGreen() + 1);
                        }
                    }
                // 检查排放标准（国五以上）- 兼容不同表达方式
                if (isEmissionStandardAboveGuoWu(vehicle.getEmissionStandard())) {
                    statistics.setBasketEmissionStandard(statistics.getBasketEmissionStandard() + 1);
                }
            } else if (vehicle.getVehicleCategory() == Vehicle.VehicleCategory.非菜篮子工程车) {
                if (vehicle.getVehicleType() == Vehicle.VehicleType.普通) {
                    if (vehicle.getColorPlate() == Vehicle.ColorPlate.蓝牌) {
                        statistics.setRegularFreightBlue(statistics.getRegularFreightBlue() + 1);
                    } else if (vehicle.getColorPlate() == Vehicle.ColorPlate.黄牌) {
                        statistics.setRegularFreightYellow(statistics.getRegularFreightYellow() + 1);
                    } else if (vehicle.getColorPlate() == Vehicle.ColorPlate.绿牌) {
                        statistics.setRegularFreightGreen(statistics.getRegularFreightGreen() + 1);
                    }
                } else if (vehicle.getVehicleType() == Vehicle.VehicleType.冷藏) {
                    if (vehicle.getColorPlate() == Vehicle.ColorPlate.蓝牌) {
                        statistics.setColdFreightBlue(statistics.getColdFreightBlue() + 1);
                    } else if (vehicle.getColorPlate() == Vehicle.ColorPlate.黄牌) {
                        statistics.setColdFreightYellow(statistics.getColdFreightYellow() + 1);
                    } else if (vehicle.getColorPlate() == Vehicle.ColorPlate.绿牌) {
                        statistics.setColdFreightGreen(statistics.getColdFreightGreen() + 1);
                    }
                }
                // 检查排放标准（国五以上）- 兼容不同表达方式
                if (isEmissionStandardAboveGuoWu(vehicle.getEmissionStandard())) {
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
            existingStats.setRegularBasketGreen(statistics.getRegularBasketGreen());
            existingStats.setColdBasketGreen(statistics.getColdBasketGreen());
            existingStats.setBasketEmissionStandard(statistics.getBasketEmissionStandard());
            existingStats.setRegularFreightBlue(statistics.getRegularFreightBlue());
            existingStats.setRegularFreightYellow(statistics.getRegularFreightYellow());
            existingStats.setColdFreightBlue(statistics.getColdFreightBlue());
            existingStats.setColdFreightYellow(statistics.getColdFreightYellow());
            existingStats.setRegularFreightGreen(statistics.getRegularFreightGreen());
            existingStats.setColdFreightGreen(statistics.getColdFreightGreen());
            existingStats.setFreightEmissionStandard(statistics.getFreightEmissionStandard());
            VehicleStatistics updated = vehicleStatisticsRepository.save(existingStats);
            return convertToDTO(updated);
        } else {
            VehicleStatistics saved = vehicleStatisticsRepository.save(statistics);
            return convertToDTO(saved);
        }
    }

    /**
     * 判断排放标准是否为国五及以上
     * 兼容多种表达方式：国五、国5、国六、国6、国6b、国V、国VI、国VIb等
     */
    private boolean isEmissionStandardAboveGuoWu(String emissionStandard) {
        if (emissionStandard == null || emissionStandard.trim().isEmpty()) {
            return false;
        }
        
        String standard = emissionStandard.trim().toUpperCase();
        
        // 检查是否包含国五或国5（包括国V）
        if (standard.contains("国五") || standard.contains("国5") || 
            standard.contains("国V") || standard.contains("GUOWU") ||
            standard.contains("GUO5")) {
            return true;
        }
        
        // 检查是否包含国六或国6（包括国VI、国6b等）
        if (standard.contains("国六") || standard.contains("国6") || 
            standard.contains("国VI") || standard.contains("GUOLIU") ||
            standard.contains("GUO6")) {
            return true;
        }
        
        // 检查是否包含数字5或6（在"国"字后面）
        if (standard.contains("国")) {
            int guoIndex = standard.indexOf("国");
            if (guoIndex >= 0 && guoIndex + 1 < standard.length()) {
                char nextChar = standard.charAt(guoIndex + 1);
                // 检查是否是5、6或V、VI
                if (nextChar == '5' || nextChar == '6' || 
                    (guoIndex + 2 < standard.length() && 
                     (standard.substring(guoIndex + 1, guoIndex + 3).equals("VI") ||
                      standard.substring(guoIndex + 1, guoIndex + 3).equals("V")))) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private VehicleStatisticsDTO convertToDTO(VehicleStatistics vehicleStatistics) {
        VehicleStatisticsDTO dto = new VehicleStatisticsDTO();
        dto.setId(vehicleStatistics.getId());
        dto.setCompanyId(vehicleStatistics.getCompany().getId());
        dto.setCompanyName(vehicleStatistics.getCompany().getName());
        dto.setStatDate(vehicleStatistics.getStatDate());
        dto.setRegularBasketBlue(vehicleStatistics.getRegularBasketBlue());
        dto.setRegularBasketYellow(vehicleStatistics.getRegularBasketYellow());
        dto.setRegularBasketGreen(vehicleStatistics.getRegularBasketGreen());
        dto.setColdBasketBlue(vehicleStatistics.getColdBasketBlue());
        dto.setColdBasketYellow(vehicleStatistics.getColdBasketYellow());
        dto.setColdBasketGreen(vehicleStatistics.getColdBasketGreen());
        dto.setBasketEmissionStandard(vehicleStatistics.getBasketEmissionStandard());
        dto.setRegularFreightBlue(vehicleStatistics.getRegularFreightBlue());
        dto.setRegularFreightYellow(vehicleStatistics.getRegularFreightYellow());
        dto.setRegularFreightGreen(vehicleStatistics.getRegularFreightGreen());
        dto.setColdFreightBlue(vehicleStatistics.getColdFreightBlue());
        dto.setColdFreightYellow(vehicleStatistics.getColdFreightYellow());
        dto.setColdFreightGreen(vehicleStatistics.getColdFreightGreen());
        dto.setFreightEmissionStandard(vehicleStatistics.getFreightEmissionStandard());
        return dto;
    }
}

