package com.example.cailanzi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "survey_operational_problems",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"survey_id", "problem_type"}),
                @UniqueConstraint(columnNames = {"survey_id", "sort_order"})
        })
public class SurveyOperationalProblem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private SurveyQuestionnaire survey;

    @Enumerated(EnumType.STRING)
    @Column(name = "problem_type", nullable = false)
    private ProblemType problemType;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "other_description", length = 500)
    private String otherDescription;

    public enum ProblemType {
        企业经营下滑无法重新更换新车, 高峰时段限行无法进城, 限行区外缺少合法卸货点, 社区商超地下停车场限高禁入, 卸货车位被社会车占用, 交警城管处罚频繁, 新能源续航不足冬季掉电, 新能源充电桩少, 冷藏车购置维护成本高, 司机短缺工资上涨, 客户账期长现金流压力, 回程空驶率高, 无法进行尾板箱体分割等改装, 其他
    }
}

