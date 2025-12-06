package com.example.cailanzi.repository;

import com.example.cailanzi.entity.TransportStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportStatsRepository extends JpaRepository<TransportStats, Long> {

    Optional<TransportStats> findByVehicleIdAndStatYearAndStatMonth(Long vehicleId, Integer statYear, Integer statMonth);

    List<TransportStats> findByVehicleId(Long vehicleId);

    List<TransportStats> findByStatYearAndStatMonth(Integer statYear, Integer statMonth);

    @Query("SELECT ts FROM TransportStats ts WHERE ts.vehicle.company.id = :companyId")
    List<TransportStats> findByCompanyId(Long companyId);
}
