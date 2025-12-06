package com.example.cailanzi.repository;

import com.example.cailanzi.entity.SurveyStandardEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyStandardEquipmentRepository extends JpaRepository<SurveyStandardEquipment, Long> {
    
    List<SurveyStandardEquipment> findBySurveyId(Long surveyId);
    
    void deleteBySurveyId(Long surveyId);
}

