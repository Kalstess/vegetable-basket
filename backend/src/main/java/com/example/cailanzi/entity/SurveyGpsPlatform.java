package com.example.cailanzi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "survey_gps_platforms",
        uniqueConstraints = @UniqueConstraint(columnNames = {"survey_id", "platform_type"}))
public class SurveyGpsPlatform extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private SurveyQuestionnaire survey;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform_type", nullable = false)
    private PlatformType platformType;

    @Column(name = "other_platform", length = 500)
    private String otherPlatform;

    public enum PlatformType {
        全国道路货运车辆公共监管与服务平台简称全国货运平台, 上海主副食品市场运行调控系统, 企业自建或第三方车队管理系统TMS, 甲方货主或物流平台的运力协同系统, 上海交通局执法支队监管平台, 未接入任何平台, 其他
    }
}

