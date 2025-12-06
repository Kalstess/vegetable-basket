package com.example.cailanzi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "survey_standard_equipment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"survey_id", "equipment_type"}))
public class SurveyStandardEquipment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private SurveyQuestionnaire survey;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_type", nullable = false)
    private EquipmentType equipmentType;

    @Column(name = "other_description", length = 500)
    private String otherDescription;

    public enum EquipmentType {
        标准周转箱,
        标准周转筐,
        标准托盘,
        标准笼车,
        没有使用标准化载具,
        其他
    }
}

