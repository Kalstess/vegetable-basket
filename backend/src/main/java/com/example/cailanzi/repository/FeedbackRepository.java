package com.example.cailanzi.repository;

import com.example.cailanzi.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByCompanyId(Long companyId);

    List<Feedback> findByCompanyIdAndReportYearAndReportMonth(Long companyId, Integer reportYear, Integer reportMonth);

    List<Feedback> findByStatus(Feedback.FeedbackStatus status);

    long countByStatus(Feedback.FeedbackStatus status);

    @Query("SELECT f FROM Feedback f WHERE f.vehicle.id = :vehicleId")
    List<Feedback> findByVehicleId(Long vehicleId);
}
