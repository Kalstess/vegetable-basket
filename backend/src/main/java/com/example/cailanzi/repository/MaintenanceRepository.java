package com.example.cailanzi.repository;

import com.example.cailanzi.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByVehicleId(Long vehicleId);

    List<Maintenance> findByVehicleIdOrderByMaintDateDesc(Long vehicleId);

    List<Maintenance> findByMaintDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT m FROM Maintenance m WHERE m.vehicle.company.id = :companyId")
    List<Maintenance> findByCompanyId(Long companyId);
}
