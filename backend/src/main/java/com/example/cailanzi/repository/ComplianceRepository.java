package com.example.cailanzi.repository;

import com.example.cailanzi.entity.Compliance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplianceRepository extends JpaRepository<Compliance, Long> {

    Optional<Compliance> findByVehicleIdAndReportYearAndReportMonth(Long vehicleId, Integer reportYear, Integer reportMonth);

    List<Compliance> findByVehicleId(Long vehicleId);

    List<Compliance> findByReportYearAndReportMonth(Integer reportYear, Integer reportMonth);

    @Query("SELECT c FROM Compliance c WHERE c.vehicle.company.id = :companyId")
    List<Compliance> findByCompanyId(Long companyId);
}
