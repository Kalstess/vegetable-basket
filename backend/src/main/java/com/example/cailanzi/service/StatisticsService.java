package com.example.cailanzi.service;

import com.example.cailanzi.dto.StatisticsDTO;
import com.example.cailanzi.entity.Feedback;
import com.example.cailanzi.repository.CompanyRepository;
import com.example.cailanzi.repository.FeedbackRepository;
import com.example.cailanzi.repository.TransportStatsRepository;
import com.example.cailanzi.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final CompanyRepository companyRepository;
    private final VehicleRepository vehicleRepository;
    private final TransportStatsRepository transportStatsRepository;
    private final FeedbackRepository feedbackRepository;

    public StatisticsDTO getStatistics() {
        Long companyCount = companyRepository.count();
        Long vehicleCount = vehicleRepository.count();
        Long transportCount = transportStatsRepository.count();
        Long feedbackCount = feedbackRepository.countByStatus(Feedback.FeedbackStatus.待处理);

        return new StatisticsDTO(companyCount, vehicleCount, transportCount, feedbackCount);
    }
}

