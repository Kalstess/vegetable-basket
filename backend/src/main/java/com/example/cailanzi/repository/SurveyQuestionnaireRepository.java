package com.example.cailanzi.repository;

import com.example.cailanzi.entity.SurveyQuestionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyQuestionnaireRepository extends JpaRepository<SurveyQuestionnaire, Long> {
    
    Optional<SurveyQuestionnaire> findByCompanyIdAndSurveyYear(Long companyId, Integer surveyYear);
    
    List<SurveyQuestionnaire> findByCompanyId(Long companyId);
    
    List<SurveyQuestionnaire> findBySurveyYear(Integer surveyYear);
    
    List<SurveyQuestionnaire> findBySubmitStatus(SurveyQuestionnaire.SubmitStatus submitStatus);
}

