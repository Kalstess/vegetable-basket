package com.example.cailanzi.service;

import com.example.cailanzi.dto.FeedbackDTO;
import com.example.cailanzi.entity.Company;
import com.example.cailanzi.entity.Feedback;
import com.example.cailanzi.entity.Vehicle;
import com.example.cailanzi.repository.CompanyRepository;
import com.example.cailanzi.repository.FeedbackRepository;
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
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final CompanyRepository companyRepository;
    private final VehicleRepository vehicleRepository;

    public List<FeedbackDTO> findAll() {
        return feedbackRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<FeedbackDTO> findById(Long id) {
        return feedbackRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<FeedbackDTO> findByCompanyId(Long companyId) {
        return feedbackRepository.findByCompanyId(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FeedbackDTO> findByStatus(Feedback.FeedbackStatus status) {
        return feedbackRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FeedbackDTO save(Feedback feedback) {
        // 检查企业是否存在
        Company company = companyRepository.findById(feedback.getCompany().getId())
                .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + feedback.getCompany().getId()));

        feedback.setCompany(company);

        // 如果指定了车辆，检查车辆是否存在
        if (feedback.getVehicle() != null && feedback.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(feedback.getVehicle().getId())
                    .orElseThrow(() -> new IllegalArgumentException("车辆不存在，ID: " + feedback.getVehicle().getId()));
            feedback.setVehicle(vehicle);
        }

        Feedback saved = feedbackRepository.save(feedback);
        return convertToDTO(saved);
    }

    public FeedbackDTO update(Long id, Feedback feedbackDetails) {
        return feedbackRepository.findById(id)
                .map(feedback -> {
                    feedback.setReportYear(feedbackDetails.getReportYear());
                    feedback.setReportMonth(feedbackDetails.getReportMonth());
                    feedback.setMainOperationalDifficulties(feedbackDetails.getMainOperationalDifficulties());
                    feedback.setPolicySuggestions(feedbackDetails.getPolicySuggestions());
                    feedback.setContactPerson(feedbackDetails.getContactPerson());
                    feedback.setContactPhone(feedbackDetails.getContactPhone());
                    feedback.setStatus(feedbackDetails.getStatus());
                    feedback.setResponse(feedbackDetails.getResponse());

                    // 如果修改了车辆
                    if (feedbackDetails.getVehicle() != null && feedbackDetails.getVehicle().getId() != null) {
                        Vehicle vehicle = vehicleRepository.findById(feedbackDetails.getVehicle().getId())
                                .orElseThrow(() -> new IllegalArgumentException("车辆不存在，ID: " + feedbackDetails.getVehicle().getId()));
                        feedback.setVehicle(vehicle);
                    } else {
                        feedback.setVehicle(null);
                    }

                    return convertToDTO(feedbackRepository.save(feedback));
                })
                .orElseThrow(() -> new IllegalArgumentException("反馈记录不存在，ID: " + id));
    }

    public void deleteById(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new IllegalArgumentException("反馈记录不存在，ID: " + id);
        }
        feedbackRepository.deleteById(id);
    }

    private FeedbackDTO convertToDTO(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setCompanyId(feedback.getCompany().getId());
        dto.setCompanyName(feedback.getCompany().getName());
        if (feedback.getVehicle() != null) {
            dto.setVehicleId(feedback.getVehicle().getId());
            dto.setPlateNumber(feedback.getVehicle().getPlateNumber());
        }
        dto.setReportYear(feedback.getReportYear());
        dto.setReportMonth(feedback.getReportMonth());
        dto.setMainOperationalDifficulties(feedback.getMainOperationalDifficulties());
        dto.setPolicySuggestions(feedback.getPolicySuggestions());
        dto.setContactPerson(feedback.getContactPerson());
        dto.setContactPhone(feedback.getContactPhone());
        dto.setStatus(feedback.getStatus());
        dto.setResponse(feedback.getResponse());
        return dto;
    }
}
