package com.example.cailanzi.repository;

import com.example.cailanzi.entity.DeliveryPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryPointRepository extends JpaRepository<DeliveryPoint, Long> {

    List<DeliveryPoint> findByCompanyId(Long companyId);

    List<DeliveryPoint> findByCompanyIdAndIsActive(Long companyId, Boolean isActive);

    List<DeliveryPoint> findByVehicleId(Long vehicleId);

    List<DeliveryPoint> findByVehicleIdAndIsActive(Long vehicleId, Boolean isActive);

    List<DeliveryPoint> findByPointType(DeliveryPoint.PointType pointType);
}
