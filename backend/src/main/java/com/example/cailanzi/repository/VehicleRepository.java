package com.example.cailanzi.repository;

import com.example.cailanzi.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByCompanyId(Long companyId);

    List<Vehicle> findByCompanyIdAndIsActive(Long companyId, Boolean isActive);

    Optional<Vehicle> findByPlateNumberAndCompanyId(String plateNumber, Long companyId);

    List<Vehicle> findByVehicleCategoryAndIsActive(Vehicle.VehicleCategory category, Boolean isActive);

    @Modifying
    @Query("UPDATE Vehicle v SET v.isActive = :isActive WHERE v.id = :id")
    void updateStatus(Long id, Boolean isActive);

    long countByCompanyIdAndIsActive(Long companyId, Boolean isActive);
}