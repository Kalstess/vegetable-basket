package com.example.cailanzi.repository;

import com.example.cailanzi.entity.SurveyDeliveryCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyDeliveryCustomerRepository extends JpaRepository<SurveyDeliveryCustomer, Long> {
    
    List<SurveyDeliveryCustomer> findBySurveyId(Long surveyId);
    
    void deleteBySurveyId(Long surveyId);
}

