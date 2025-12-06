package com.example.cailanzi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "survey_delivery_customers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"survey_id", "customer_type"}))
public class SurveyDeliveryCustomer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private SurveyQuestionnaire survey;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    private CustomerType customerType;

    public enum CustomerType {
        电商平台, 连锁商超, 餐饮及中央厨房, 批发市场, 食品加工与生产企业, 食堂学校机关企业, 个人团购自提点等, 其他
    }
}

