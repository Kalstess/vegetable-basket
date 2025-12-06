package com.example.cailanzi.service;

import com.example.cailanzi.dto.TransportStatsDTO;
import com.example.cailanzi.entity.TransportStats;
import com.example.cailanzi.entity.Vehicle;
import com.example.cailanzi.repository.TransportStatsRepository;
import com.example.cailanzi.repository.VehicleRepository;
import com.example.cailanzi.util.JsonUtils;
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
public class TransportService {

    private final TransportStatsRepository transportStatsRepository;
    private final VehicleRepository vehicleRepository;

    public List<TransportStatsDTO> findAll() {
        return transportStatsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TransportStatsDTO> findById(Long id) {
        return transportStatsRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<TransportStatsDTO> findByVehicleId(Long vehicleId) {
        return transportStatsRepository.findByVehicleId(vehicleId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TransportStatsDTO> findByCompanyId(Long companyId) {
        return transportStatsRepository.findByCompanyId(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TransportStatsDTO save(TransportStats transportStats) {
        // 检查车辆是否存在
        Vehicle vehicle = vehicleRepository.findById(transportStats.getVehicle().getId())
                .orElseThrow(() -> new IllegalArgumentException("车辆不存在，ID: " + transportStats.getVehicle().getId()));

        // 检查是否已存在相同年月的记录
        Optional<TransportStats> existing = transportStatsRepository.findByVehicleIdAndStatYearAndStatMonth(
                vehicle.getId(), transportStats.getStatYear(), transportStats.getStatMonth());
        if (existing.isPresent() && !existing.get().getId().equals(transportStats.getId())) {
            throw new IllegalArgumentException("该车辆在该年月已存在运输统计记录");
        }

        // 处理productTypes：如果是List类型，转换为JSON字符串
        String productTypesStr = convertProductTypesToString(transportStats.getProductTypes());
        transportStats.setProductTypes(productTypesStr);

        // 确保新字段被正确设置
        log.debug("保存运输统计 - 月配送费用: {}, 日配送网点数: {}", 
                transportStats.getMonthDeliveryCost(), transportStats.getDailyDeliveryPoints());

        transportStats.setVehicle(vehicle);
        TransportStats saved = transportStatsRepository.save(transportStats);
        return convertToDTO(saved);
    }

    public TransportStatsDTO update(Long id, TransportStats transportStatsDetails) {
        return transportStatsRepository.findById(id)
                .map(transportStats -> {
                    // 如果修改了年月，检查是否重复
                    if (!transportStats.getStatYear().equals(transportStatsDetails.getStatYear()) ||
                            !transportStats.getStatMonth().equals(transportStatsDetails.getStatMonth())) {
                        Optional<TransportStats> existing = transportStatsRepository.findByVehicleIdAndStatYearAndStatMonth(
                                transportStats.getVehicle().getId(),
                                transportStatsDetails.getStatYear(),
                                transportStatsDetails.getStatMonth());
                        if (existing.isPresent() && !existing.get().getId().equals(id)) {
                            throw new IllegalArgumentException("该车辆在该年月已存在运输统计记录");
                        }
                    }

                    transportStats.setStatYear(transportStatsDetails.getStatYear());
                    transportStats.setStatMonth(transportStatsDetails.getStatMonth());
                    transportStats.setDailyDeliveryTimes(transportStatsDetails.getDailyDeliveryTimes());
                    transportStats.setMonthProductTon(transportStatsDetails.getMonthProductTon());
                    transportStats.setMonthKilometers(transportStatsDetails.getMonthKilometers());
                    transportStats.setMonthDeliveryCost(transportStatsDetails.getMonthDeliveryCost());
                    transportStats.setDailyDeliveryPoints(transportStatsDetails.getDailyDeliveryPoints());
                    transportStats.setPeakSeasonDailyIncreaseTimes(transportStatsDetails.getPeakSeasonDailyIncreaseTimes());
                    
                    // 处理productTypes：如果是List类型，转换为JSON字符串
                    String productTypesStr = convertProductTypesToString(transportStatsDetails.getProductTypes());
                    transportStats.setProductTypes(productTypesStr);

                    return convertToDTO(transportStatsRepository.save(transportStats));
                })
                .orElseThrow(() -> new IllegalArgumentException("运输统计记录不存在，ID: " + id));
    }

    public void deleteById(Long id) {
        if (!transportStatsRepository.existsById(id)) {
            throw new IllegalArgumentException("运输统计记录不存在，ID: " + id);
        }
        transportStatsRepository.deleteById(id);
    }

    private TransportStatsDTO convertToDTO(TransportStats transportStats) {
        TransportStatsDTO dto = new TransportStatsDTO();
        dto.setId(transportStats.getId());
        dto.setVehicleId(transportStats.getVehicle().getId());
        dto.setPlateNumber(transportStats.getVehicle().getPlateNumber());
        dto.setCompanyName(transportStats.getVehicle().getCompany().getName());
        dto.setStatYear(transportStats.getStatYear());
        dto.setStatMonth(transportStats.getStatMonth());
        dto.setDailyDeliveryTimes(transportStats.getDailyDeliveryTimes());
        dto.setMonthProductTon(transportStats.getMonthProductTon());
        dto.setMonthKilometers(transportStats.getMonthKilometers());
        dto.setMonthDeliveryCost(transportStats.getMonthDeliveryCost());
        dto.setDailyDeliveryPoints(transportStats.getDailyDeliveryPoints());
        dto.setPeakSeasonDailyIncreaseTimes(transportStats.getPeakSeasonDailyIncreaseTimes());
        if (transportStats.getProductTypes() != null) {
            dto.setProductTypes(JsonUtils.fromJsonArray(transportStats.getProductTypes(), String.class));
        }
        return dto;
    }

    /**
     * 将productTypes转换为JSON字符串
     * 支持List<String>和String类型
     */
    @SuppressWarnings("unchecked")
    private String convertProductTypesToString(Object productTypes) {
        if (productTypes == null) {
            return null;
        }
        if (productTypes instanceof String) {
            // 如果已经是字符串，直接返回
            return (String) productTypes;
        }
        if (productTypes instanceof List) {
            // 如果是List，转换为JSON字符串
            return JsonUtils.toJson(productTypes);
        }
        // 其他类型，尝试转换为JSON字符串
        return JsonUtils.toJson(productTypes);
    }
}
