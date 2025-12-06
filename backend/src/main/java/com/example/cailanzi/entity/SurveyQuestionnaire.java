package com.example.cailanzi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "survey_questionnaire",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "survey_year"}))
public class SurveyQuestionnaire extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "survey_year", nullable = false)
    private Integer surveyYear;

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "submit_status")
    private SubmitStatus submitStatus = SubmitStatus.草稿;

    @Column(name = "submitted_by")
    private String submittedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_user_id")
    private User submittedUser;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    // 基本信息部分
    @Column(name = "annual_transport_2022", precision = 12, scale = 2)
    private BigDecimal annualTransport2022;

    @Column(name = "annual_transport_2023", precision = 12, scale = 2)
    private BigDecimal annualTransport2023;

    @Column(name = "annual_transport_2024", precision = 12, scale = 2)
    private BigDecimal annualTransport2024;

    @Column(name = "annual_transport_2025", precision = 12, scale = 2)
    private BigDecimal annualTransport2025;

    @Enumerated(EnumType.STRING)
    @Column(name = "future_vehicle_plan")
    private FutureVehiclePlan futureVehiclePlan;

    @Column(name = "revenue_2025", precision = 14, scale = 2)
    private BigDecimal revenue2025;

    // 车辆数量部分
    @Column(name = "regular_basket_vehicles_2025")
    private Integer regularBasketVehicles2025 = 0;

    @Column(name = "cold_basket_vehicles_2025")
    private Integer coldBasketVehicles2025 = 0;

    @Column(name = "regular_other_vehicles_2025")
    private Integer regularOtherVehicles2025 = 0;

    @Column(name = "cold_other_vehicles_2025")
    private Integer coldOtherVehicles2025 = 0;

    // 出车次数比较
    @Enumerated(EnumType.STRING)
    @Column(name = "basket_trip_comparison")
    private BasketTripComparison basketTripComparison;

    // 运输产品种类
    @Enumerated(EnumType.STRING)
    @Column(name = "main_product_type")
    private ProductType mainProductType;

    @Enumerated(EnumType.STRING)
    @Column(name = "secondary_product_type")
    private ProductType secondaryProductType;

    // 卫星定位系统
    @Enumerated(EnumType.STRING)
    @Column(name = "gps_system_type")
    private GpsSystemType gpsSystemType;

    // 冷藏车相关
    @Column(name = "has_cold_basket_vehicle")
    private Boolean hasColdBasketVehicle = false;

    @Column(name = "cold_vehicle_multi_temp")
    private Boolean coldVehicleMultiTemp;

    @Enumerated(EnumType.STRING)
    @Column(name = "temp_recording_device")
    private TempRecordingDevice tempRecordingDevice;

    // 装载率
    @Enumerated(EnumType.STRING)
    @Column(name = "avg_loading_rate")
    private AvgLoadingRate avgLoadingRate;

    // 使用菜篮子车最看重的因素
    @Enumerated(EnumType.STRING)
    @Column(name = "basket_vehicle_importance")
    private BasketVehicleImportance basketVehicleImportance;

    @Column(name = "other_importance", length = 500)
    private String otherImportance;

    public enum SubmitStatus {
        草稿, 已提交, 已审核, 已驳回
    }

    public enum FutureVehiclePlan {
        增加, 减少, 不变
    }

    public enum BasketTripComparison {
        非常低, 低, 差不多, 高, 非常高
    }

    public enum ProductType {
        A生鲜蔬菜类, B肉类及其制品, C水产品类, D豆制品类, E蛋奶及其制品, F水果类, G粮食及其制品, H综合配送类
    }

    public enum GpsSystemType {
        GPS单模, 北斗单模, 北斗GPS双模, 北斗GPSGLONASSGalileo多星融合, 暂未安装任何卫星定位系统
    }

    public enum TempRecordingDevice {
        只有驾驶区有温度显示无记录, 车载温度记录仪单机无网络型, 车载温度记录仪无线网络传输型, 实时云端监控异常报警平台, 无温控记录设备, 其他
    }

    public enum AvgLoadingRate {
        小于等于50, 五十一到七十, 七十一到九十, 九十一以上
    }

    public enum BasketVehicleImportance {
        进市区通行的便利性, 高速隧道桥通行费的减免, 车身统一菜篮子工程标识带来的企业公益形象提升, 其他
    }
}

