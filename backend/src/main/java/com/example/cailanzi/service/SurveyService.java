package com.example.cailanzi.service;

import com.example.cailanzi.dto.SurveyQuestionnaireDTO;
import com.example.cailanzi.entity.*;
import com.example.cailanzi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SurveyService {

    private final SurveyQuestionnaireRepository surveyRepository;
    private final SurveyDeliveryCustomerRepository deliveryCustomerRepository;
    private final SurveyStandardEquipmentRepository standardEquipmentRepository;
    private final SurveyGpsPlatformRepository gpsPlatformRepository;
    private final SurveyOperationalProblemRepository operationalProblemRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public List<SurveyQuestionnaireDTO> findAll() {
        return surveyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<SurveyQuestionnaireDTO> findById(Long id) {
        return surveyRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<SurveyQuestionnaireDTO> findByCompanyId(Long companyId) {
        return surveyRepository.findByCompanyId(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SurveyQuestionnaireDTO> findBySurveyYear(Integer surveyYear) {
        return surveyRepository.findBySurveyYear(surveyYear).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SurveyQuestionnaireDTO save(SurveyQuestionnaireDTO dto, Long userId) {
        // 检查企业是否存在
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + dto.getCompanyId()));

        // 检查是否已存在相同企业、相同年份的记录
        Optional<SurveyQuestionnaire> existing = surveyRepository.findByCompanyIdAndSurveyYear(
                dto.getCompanyId(), dto.getSurveyYear());
        
        SurveyQuestionnaire survey;
        if (existing.isPresent() && (dto.getId() == null || !existing.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("该企业在该年份已存在问卷调查记录");
        }

        if (dto.getId() != null) {
            // 更新
            survey = surveyRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("问卷调查记录不存在，ID: " + dto.getId()));
        } else {
            // 新建
            survey = new SurveyQuestionnaire();
            survey.setSubmitTime(LocalDateTime.now());
        }

        // 设置基本信息
        survey.setCompany(company);
        survey.setSurveyYear(dto.getSurveyYear());
        survey.setAnnualTransport2022(dto.getAnnualTransport2022());
        survey.setAnnualTransport2023(dto.getAnnualTransport2023());
        survey.setAnnualTransport2024(dto.getAnnualTransport2024());
        survey.setAnnualTransport2025(dto.getAnnualTransport2025());
        survey.setFutureVehiclePlan(parseEnum(dto.getFutureVehiclePlan(), SurveyQuestionnaire.FutureVehiclePlan.class));
        survey.setRevenue2025(dto.getRevenue2025());
        survey.setRegularBasketVehicles2025(dto.getRegularBasketVehicles2025());
        survey.setColdBasketVehicles2025(dto.getColdBasketVehicles2025());
        survey.setRegularOtherVehicles2025(dto.getRegularOtherVehicles2025());
        survey.setColdOtherVehicles2025(dto.getColdOtherVehicles2025());
        survey.setBasketTripComparison(parseEnum(dto.getBasketTripComparison(), SurveyQuestionnaire.BasketTripComparison.class));
        survey.setMainProductType(parseEnum(dto.getMainProductType(), SurveyQuestionnaire.ProductType.class));
        survey.setSecondaryProductType(parseEnum(dto.getSecondaryProductType(), SurveyQuestionnaire.ProductType.class));
        survey.setGpsSystemType(parseEnum(dto.getGpsSystemType(), SurveyQuestionnaire.GpsSystemType.class));
        survey.setHasColdBasketVehicle(dto.getHasColdBasketVehicle());
        survey.setColdVehicleMultiTemp(dto.getColdVehicleMultiTemp());
        survey.setTempRecordingDevice(parseEnum(dto.getTempRecordingDevice(), SurveyQuestionnaire.TempRecordingDevice.class));
        // 将显示文本转换为枚举值
        String avgLoadingRateValue = dto.getAvgLoadingRate();
        if (avgLoadingRateValue != null) {
            String enumValue = switch (avgLoadingRateValue) {
                case "≤50%" -> "小于等于50";
                case "51-70%" -> "五十一到七十";
                case "71-90%" -> "七十一到九十";
                case "91%以上" -> "九十一以上";
                default -> avgLoadingRateValue; // 如果已经是枚举值，直接使用
            };
            survey.setAvgLoadingRate(parseEnum(enumValue, SurveyQuestionnaire.AvgLoadingRate.class));
        } else {
            survey.setAvgLoadingRate(null);
        }
        survey.setBasketVehicleImportance(parseEnum(dto.getBasketVehicleImportance(), SurveyQuestionnaire.BasketVehicleImportance.class));
        survey.setOtherImportance(dto.getOtherImportance());

        // 设置提交状态和提交人
        if (dto.getSubmitStatus() != null) {
            survey.setSubmitStatus(parseEnum(dto.getSubmitStatus(), SurveyQuestionnaire.SubmitStatus.class));
        }
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                survey.setSubmittedUser(user);
                survey.setSubmittedBy(user.getNickname() != null ? user.getNickname() : user.getUsername());
            }
        }

        // 保存主表
        SurveyQuestionnaire saved = surveyRepository.save(survey);

        // 保存关联表数据
        saveDeliveryCustomers(saved.getId(), dto.getDeliveryCustomerTypes());
        saveStandardEquipment(saved.getId(), dto.getStandardEquipmentTypes());
        saveGpsPlatforms(saved.getId(), dto.getGpsPlatformTypes());
        saveOperationalProblems(saved.getId(), dto.getOperationalProblems());

        return convertToDTO(saved);
    }

    public void deleteById(Long id) {
        if (!surveyRepository.existsById(id)) {
            throw new IllegalArgumentException("问卷调查记录不存在，ID: " + id);
        }
        
        // 先删除关联数据
        try {
            deliveryCustomerRepository.deleteBySurveyId(id);
            standardEquipmentRepository.deleteBySurveyId(id);
            gpsPlatformRepository.deleteBySurveyId(id);
            operationalProblemRepository.deleteBySurveyId(id);
        } catch (Exception e) {
            log.warn("删除问卷关联数据时出错，继续删除问卷: {}", e.getMessage());
        }
        
        // 最后删除问卷本身
        surveyRepository.deleteById(id);
    }

    public SurveyQuestionnaireDTO submit(Long id, Long userId) {
        SurveyQuestionnaire survey = surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("问卷调查记录不存在，ID: " + id));
        
        survey.setSubmitStatus(SurveyQuestionnaire.SubmitStatus.已提交);
        survey.setSubmitTime(LocalDateTime.now());
        
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                survey.setSubmittedUser(user);
                survey.setSubmittedBy(user.getNickname() != null ? user.getNickname() : user.getUsername());
            }
        }
        
        return convertToDTO(surveyRepository.save(survey));
    }

    private void saveDeliveryCustomers(Long surveyId, List<String> customerTypes) {
        if (customerTypes == null) return;
        
        // 删除旧的
        deliveryCustomerRepository.deleteBySurveyId(surveyId);
        
        // 保存新的
        SurveyQuestionnaire survey = surveyRepository.findById(surveyId).orElse(null);
        if (survey == null) return;
        
        for (String type : customerTypes) {
            try {
                SurveyDeliveryCustomer.CustomerType enumType = SurveyDeliveryCustomer.CustomerType.valueOf(type);
                SurveyDeliveryCustomer customer = new SurveyDeliveryCustomer();
                customer.setSurvey(survey);
                customer.setCustomerType(enumType);
                deliveryCustomerRepository.save(customer);
            } catch (IllegalArgumentException e) {
                log.warn("无效的客户类型: {}", type);
            }
        }
    }

    private void saveStandardEquipment(Long surveyId, List<String> equipmentTypes) {
        if (equipmentTypes == null) return;
        
        standardEquipmentRepository.deleteBySurveyId(surveyId);
        
        SurveyQuestionnaire survey = surveyRepository.findById(surveyId).orElse(null);
        if (survey == null) return;
        
        for (String type : equipmentTypes) {
            try {
                SurveyStandardEquipment.EquipmentType enumType = SurveyStandardEquipment.EquipmentType.valueOf(type);
                SurveyStandardEquipment equipment = new SurveyStandardEquipment();
                equipment.setSurvey(survey);
                equipment.setEquipmentType(enumType);
                standardEquipmentRepository.save(equipment);
            } catch (IllegalArgumentException e) {
                log.warn("无效的载具类型: {}", type);
            }
        }
    }

    private void saveGpsPlatforms(Long surveyId, List<String> platformTypes) {
        if (platformTypes == null) return;
        
        gpsPlatformRepository.deleteBySurveyId(surveyId);
        
        SurveyQuestionnaire survey = surveyRepository.findById(surveyId).orElse(null);
        if (survey == null) return;
        
        for (String type : platformTypes) {
            try {
                SurveyGpsPlatform.PlatformType enumType = SurveyGpsPlatform.PlatformType.valueOf(type);
                SurveyGpsPlatform platform = new SurveyGpsPlatform();
                platform.setSurvey(survey);
                platform.setPlatformType(enumType);
                gpsPlatformRepository.save(platform);
            } catch (IllegalArgumentException e) {
                log.warn("无效的平台类型: {}", type);
            }
        }
    }

    private void saveOperationalProblems(Long surveyId, List<SurveyQuestionnaireDTO.OperationalProblemDTO> problems) {
        if (problems == null) return;
        
        operationalProblemRepository.deleteBySurveyId(surveyId);
        
        SurveyQuestionnaire survey = surveyRepository.findById(surveyId).orElse(null);
        if (survey == null) return;
        
        for (SurveyQuestionnaireDTO.OperationalProblemDTO problemDTO : problems) {
            try {
                SurveyOperationalProblem.ProblemType enumType = SurveyOperationalProblem.ProblemType.valueOf(problemDTO.getProblemType());
                SurveyOperationalProblem problem = new SurveyOperationalProblem();
                problem.setSurvey(survey);
                problem.setProblemType(enumType);
                problem.setSortOrder(problemDTO.getSortOrder());
                problem.setOtherDescription(problemDTO.getOtherDescription());
                operationalProblemRepository.save(problem);
            } catch (IllegalArgumentException e) {
                log.warn("无效的问题类型: {}", problemDTO.getProblemType());
            }
        }
    }

    private SurveyQuestionnaireDTO convertToDTO(SurveyQuestionnaire survey) {
        SurveyQuestionnaireDTO dto = new SurveyQuestionnaireDTO();
        dto.setId(survey.getId());
        dto.setCompanyId(survey.getCompany().getId());
        dto.setCompanyName(survey.getCompany().getName());
        dto.setSurveyYear(survey.getSurveyYear());
        dto.setSubmitTime(survey.getSubmitTime());
        dto.setSubmitStatus(survey.getSubmitStatus() != null ? survey.getSubmitStatus().name() : null);
        dto.setSubmittedBy(survey.getSubmittedBy());
        dto.setSubmittedUserId(survey.getSubmittedUser() != null ? survey.getSubmittedUser().getId() : null);
        dto.setReviewedBy(survey.getReviewedBy());
        dto.setReviewedAt(survey.getReviewedAt());
        dto.setReviewComment(survey.getReviewComment());
        
        dto.setAnnualTransport2022(survey.getAnnualTransport2022());
        dto.setAnnualTransport2023(survey.getAnnualTransport2023());
        dto.setAnnualTransport2024(survey.getAnnualTransport2024());
        dto.setAnnualTransport2025(survey.getAnnualTransport2025());
        dto.setFutureVehiclePlan(survey.getFutureVehiclePlan() != null ? survey.getFutureVehiclePlan().name() : null);
        dto.setRevenue2025(survey.getRevenue2025());
        dto.setRegularBasketVehicles2025(survey.getRegularBasketVehicles2025());
        dto.setColdBasketVehicles2025(survey.getColdBasketVehicles2025());
        dto.setRegularOtherVehicles2025(survey.getRegularOtherVehicles2025());
        dto.setColdOtherVehicles2025(survey.getColdOtherVehicles2025());
        dto.setBasketTripComparison(survey.getBasketTripComparison() != null ? survey.getBasketTripComparison().name() : null);
        dto.setMainProductType(survey.getMainProductType() != null ? survey.getMainProductType().name() : null);
        dto.setSecondaryProductType(survey.getSecondaryProductType() != null ? survey.getSecondaryProductType().name() : null);
        dto.setGpsSystemType(survey.getGpsSystemType() != null ? survey.getGpsSystemType().name() : null);
        dto.setHasColdBasketVehicle(survey.getHasColdBasketVehicle());
        dto.setColdVehicleMultiTemp(survey.getColdVehicleMultiTemp());
        dto.setTempRecordingDevice(survey.getTempRecordingDevice() != null ? survey.getTempRecordingDevice().name() : null);
        // 将枚举值转换为显示文本
        if (survey.getAvgLoadingRate() != null) {
            String displayText = switch (survey.getAvgLoadingRate()) {
                case 小于等于50 -> "≤50%";
                case 五十一到七十 -> "51-70%";
                case 七十一到九十 -> "71-90%";
                case 九十一以上 -> "91%以上";
            };
            dto.setAvgLoadingRate(displayText);
        } else {
            dto.setAvgLoadingRate(null);
        }
        dto.setBasketVehicleImportance(survey.getBasketVehicleImportance() != null ? survey.getBasketVehicleImportance().name() : null);
        dto.setOtherImportance(survey.getOtherImportance());

        // 加载关联数据
        dto.setDeliveryCustomerTypes(
                deliveryCustomerRepository.findBySurveyId(survey.getId()).stream()
                        .map(c -> c.getCustomerType().name())
                        .collect(Collectors.toList())
        );
        dto.setStandardEquipmentTypes(
                standardEquipmentRepository.findBySurveyId(survey.getId()).stream()
                        .map(e -> e.getEquipmentType().name())
                        .collect(Collectors.toList())
        );
        dto.setGpsPlatformTypes(
                gpsPlatformRepository.findBySurveyId(survey.getId()).stream()
                        .map(p -> p.getPlatformType().name())
                        .collect(Collectors.toList())
        );
        dto.setOperationalProblems(
                operationalProblemRepository.findBySurveyIdOrderBySortOrderAsc(survey.getId()).stream()
                        .map(p -> {
                            SurveyQuestionnaireDTO.OperationalProblemDTO problemDTO = new SurveyQuestionnaireDTO.OperationalProblemDTO();
                            problemDTO.setProblemType(p.getProblemType().name());
                            problemDTO.setSortOrder(p.getSortOrder());
                            problemDTO.setOtherDescription(p.getOtherDescription());
                            return problemDTO;
                        })
                        .collect(Collectors.toList())
        );

        return dto;
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> T parseEnum(String value, Class<T> enumClass) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            log.warn("无法解析枚举值: {} for {}", value, enumClass.getSimpleName());
            return null;
        }
    }
}