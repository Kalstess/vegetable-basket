package com.example.cailanzi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SurveyQuestionnaireDTO {
    private Long id;
    private Long companyId;
    private String companyName;
    private Integer surveyYear;
    private LocalDateTime submitTime;
    private String submitStatus;
    private String submittedBy;
    private Long submittedUserId;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewComment;

    // 基本信息部分
    private BigDecimal annualTransport2022;
    private BigDecimal annualTransport2023;
    private BigDecimal annualTransport2024;
    private BigDecimal annualTransport2025;
    private String futureVehiclePlan;
    private BigDecimal revenue2025;

    // 车辆数量部分
    private Integer regularBasketVehicles2025;
    private Integer coldBasketVehicles2025;
    private Integer regularOtherVehicles2025;
    private Integer coldOtherVehicles2025;

    // 出车次数比较
    private String basketTripComparison;

    // 运输产品种类
    private String mainProductType;
    private String secondaryProductType;

    // 卫星定位系统
    private String gpsSystemType;

    // 冷藏车相关
    private Boolean hasColdBasketVehicle;
    private Boolean coldVehicleMultiTemp;
    private String tempRecordingDevice;

    // 装载率
    private String avgLoadingRate;

    // 使用菜篮子车最看重的因素
    private String basketVehicleImportance;
    private String otherImportance;

    // 多选答案
    private List<String> deliveryCustomerTypes;
    private List<String> standardEquipmentTypes;
    private List<String> gpsPlatformTypes;
    private List<OperationalProblemDTO> operationalProblems;

    @Data
    public static class OperationalProblemDTO {
        private String problemType;
        private Integer sortOrder;
        private String otherDescription;
    }
}

