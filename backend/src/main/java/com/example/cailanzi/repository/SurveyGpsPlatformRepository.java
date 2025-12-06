package com.example.cailanzi.repository;

import com.example.cailanzi.entity.SurveyGpsPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyGpsPlatformRepository extends JpaRepository<SurveyGpsPlatform, Long> {
    
    List<SurveyGpsPlatform> findBySurveyId(Long surveyId);
    
    void deleteBySurveyId(Long surveyId);
}

