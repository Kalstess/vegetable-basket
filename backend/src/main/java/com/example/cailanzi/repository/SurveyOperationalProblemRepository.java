package com.example.cailanzi.repository;

import com.example.cailanzi.entity.SurveyOperationalProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyOperationalProblemRepository extends JpaRepository<SurveyOperationalProblem, Long> {
    
    List<SurveyOperationalProblem> findBySurveyIdOrderBySortOrderAsc(Long surveyId);
    
    void deleteBySurveyId(Long surveyId);
}

