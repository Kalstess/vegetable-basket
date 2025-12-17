package com.example.cailanzi.service;

import com.example.cailanzi.dto.CompanyTransportTrendDTO;
import com.example.cailanzi.dto.VehicleYearStatsDTO;
import com.example.cailanzi.entity.Company;
import com.example.cailanzi.entity.SurveyQuestionnaire;
import com.example.cailanzi.entity.TransportStats;
import com.example.cailanzi.entity.Vehicle;
import com.example.cailanzi.repository.SurveyQuestionnaireRepository;
import com.example.cailanzi.repository.TransportStatsRepository;
import com.example.cailanzi.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final SurveyQuestionnaireRepository surveyRepository;
    private final TransportStatsRepository transportStatsRepository;
    private final VehicleRepository vehicleRepository;

    /**
     * 企业 2022-2025 年运输量与营收趋势分析
     */
    public List<CompanyTransportTrendDTO> getCompanyTransportTrends(Long companyId) {
        List<SurveyQuestionnaire> surveys;
        if (companyId != null) {
            surveys = surveyRepository.findByCompanyId(companyId);
        } else {
            surveys = surveyRepository.findAll();
        }

        // 每个公司只保留最新一条问卷（按 surveyYear 最大）
        Map<Long, SurveyQuestionnaire> latestByCompany = surveys.stream()
                .filter(s -> s.getCompany() != null)
                .collect(Collectors.toMap(
                        s -> s.getCompany().getId(),
                        s -> s,
                        (s1, s2) -> s1.getSurveyYear() >= s2.getSurveyYear() ? s1 : s2
                ));

        List<CompanyTransportTrendDTO> result = new ArrayList<>();
        for (SurveyQuestionnaire s : latestByCompany.values()) {
            CompanyTransportTrendDTO dto = new CompanyTransportTrendDTO();
            Company c = s.getCompany();
            dto.setCompanyId(c.getId());
            dto.setCompanyName(c.getName());

            BigDecimal t2022 = safeDecimal(s.getAnnualTransport2022());
            BigDecimal t2023 = safeDecimal(s.getAnnualTransport2023());
            BigDecimal t2024 = safeDecimal(s.getAnnualTransport2024());
            BigDecimal t2025 = safeDecimal(s.getAnnualTransport2025());

            dto.setTransport2022(t2022);
            dto.setTransport2023(t2023);
            dto.setTransport2024(t2024);
            dto.setTransport2025(t2025);
            dto.setRevenue2025(safeDecimal(s.getRevenue2025()));

            dto.setYoy2023(calcYoy(t2022, t2023));
            dto.setYoy2024(calcYoy(t2023, t2024));
            dto.setYoy2025(calcYoy(t2024, t2025));
            dto.setCagr(calcCagr(t2022, t2025, 3));
            dto.setTrendType(determineTrendType(Arrays.asList(t2022, t2023, t2024, t2025)));

            result.add(dto);
        }
        return result;
    }

    /**
     * 企业内车辆年度运营统计
     */
    public List<VehicleYearStatsDTO> getVehicleYearStats(Long companyId, Integer year) {
        if (companyId == null || year == null) {
            return Collections.emptyList();
        }
        // 找出该企业所有有效车辆
        List<Vehicle> vehicles = vehicleRepository.findByCompanyIdAndIsActive(companyId, true);
        if (vehicles.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Vehicle> vehicleMap = vehicles.stream()
                .collect(Collectors.toMap(Vehicle::getId, v -> v));

        // 查该企业全部运单统计（按公司过滤）再按年份筛选
        List<TransportStats> stats = transportStatsRepository.findByCompanyId(companyId).stream()
                .filter(ts -> Objects.equals(ts.getStatYear(), year))
                .collect(Collectors.toList());

        Map<Long, VehicleYearStatsDTO> dtoMap = new HashMap<>();
        for (TransportStats ts : stats) {
            Vehicle v = ts.getVehicle();
            if (v == null || !vehicleMap.containsKey(v.getId())) {
                continue;
            }
            VehicleYearStatsDTO dto = dtoMap.computeIfAbsent(v.getId(), id -> {
                VehicleYearStatsDTO d = new VehicleYearStatsDTO();
                d.setVehicleId(v.getId());
                d.setPlateNumber(v.getPlateNumber());
                d.setCompanyId(v.getCompany().getId());
                d.setCompanyName(v.getCompany().getName());
                d.setVehicleType(v.getVehicleType() != null ? v.getVehicleType().name() : null);
                d.setYearTransportTon(BigDecimal.ZERO);
                d.setYearKilometers(BigDecimal.ZERO);
                d.setYearDeliveryCost(BigDecimal.ZERO);
                d.setTonKilometerEfficiency(BigDecimal.ZERO);
                return d;
            });

            BigDecimal monthTon = safeDecimal(ts.getMonthProductTon());
            BigDecimal monthKm = safeDecimal(ts.getMonthKilometers());
            BigDecimal monthCost = safeDecimal(ts.getMonthDeliveryCost());

            dto.setYearTransportTon(dto.getYearTransportTon().add(monthTon));
            dto.setYearKilometers(dto.getYearKilometers().add(monthKm));
            dto.setYearDeliveryCost(dto.getYearDeliveryCost().add(monthCost));
        }

        // 计算吨公里效率
        dtoMap.values().forEach(dto -> {
            if (dto.getYearTransportTon().compareTo(BigDecimal.ZERO) > 0 &&
                    dto.getYearKilometers().compareTo(BigDecimal.ZERO) > 0) {
                dto.setTonKilometerEfficiency(
                        dto.getYearTransportTon().multiply(dto.getYearKilometers())
                                .setScale(2, RoundingMode.HALF_UP)
                );
            } else {
                dto.setTonKilometerEfficiency(BigDecimal.ZERO);
            }
        });

        List<VehicleYearStatsDTO> list = new ArrayList<>(dtoMap.values());

        // 计算占比
        BigDecimal totalTon = list.stream()
                .map(VehicleYearStatsDTO::getYearTransportTon)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalKm = list.stream()
                .map(VehicleYearStatsDTO::getYearKilometers)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (VehicleYearStatsDTO dto : list) {
            if (totalTon.compareTo(BigDecimal.ZERO) > 0) {
                dto.setTransportShare(dto.getYearTransportTon()
                        .divide(totalTon, 4, RoundingMode.HALF_UP));
            } else {
                dto.setTransportShare(BigDecimal.ZERO);
            }
            if (totalKm.compareTo(BigDecimal.ZERO) > 0) {
                dto.setKilometerShare(dto.getYearKilometers()
                        .divide(totalKm, 4, RoundingMode.HALF_UP));
            } else {
                dto.setKilometerShare(BigDecimal.ZERO);
            }
        }

        // 按效率降序排名
        list.sort(Comparator.comparing(VehicleYearStatsDTO::getTonKilometerEfficiency).reversed());
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setEfficiencyRank(i + 1);
        }

        return list;
    }

    private BigDecimal safeDecimal(Number n) {
        if (n == null) return BigDecimal.ZERO;
        if (n instanceof BigDecimal) return (BigDecimal) n;
        return new BigDecimal(n.toString());
    }

    private BigDecimal calcYoy(BigDecimal prev, BigDecimal current) {
        if (prev == null || current == null) return null;
        if (prev.compareTo(BigDecimal.ZERO) == 0) return null;
        return current.subtract(prev)
                .divide(prev, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calcCagr(BigDecimal start, BigDecimal end, int years) {
        if (start == null || end == null) return null;
        if (start.compareTo(BigDecimal.ZERO) <= 0 || end.compareTo(BigDecimal.ZERO) <= 0 || years <= 0) {
            return null;
        }
        double cagr = Math.pow(end.doubleValue() / start.doubleValue(), 1.0 / years) - 1.0;
        return BigDecimal.valueOf(cagr).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * 简单趋势类型判断
     */
    private String determineTrendType(List<BigDecimal> series) {
        if (series == null || series.size() < 2) {
            return "未知";
        }
        // 用首尾比较判断整体方向
        BigDecimal first = series.get(0);
        BigDecimal last = series.get(series.size() - 1);
        BigDecimal diff = last.subtract(first);

        boolean increasing = true;
        boolean decreasing = true;
        for (int i = 1; i < series.size(); i++) {
            int cmp = series.get(i).compareTo(series.get(i - 1));
            if (cmp < 0) {
                increasing = false;
            } else if (cmp > 0) {
                decreasing = false;
            }
        }

        if (increasing && diff.compareTo(BigDecimal.ZERO) > 0) {
            return "持续增长";
        }
        if (decreasing && diff.compareTo(BigDecimal.ZERO) < 0) {
            return "持续下降";
        }
        return "波动型";
    }
}


