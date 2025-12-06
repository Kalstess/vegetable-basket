package com.example.cailanzi.repository;

import com.example.cailanzi.entity.VehicleStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VehicleStatisticsRepository extends JpaRepository<VehicleStatistics, Long> {

    Optional<VehicleStatistics> findByCompanyIdAndStatDate(Long companyId, LocalDate statDate);

    java.util.List<VehicleStatistics> findByCompanyId(Long companyId);

    java.util.List<VehicleStatistics> findByStatDate(LocalDate statDate);
}
