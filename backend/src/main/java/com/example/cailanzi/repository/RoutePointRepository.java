package com.example.cailanzi.repository;

import com.example.cailanzi.entity.RoutePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {

    List<RoutePoint> findByVehicleId(Long vehicleId);

    List<RoutePoint> findByVehicleIdAndRouteDate(Long vehicleId, LocalDate routeDate);

    List<RoutePoint> findByRouteId(String routeId);

    List<RoutePoint> findByRouteIdOrderBySeqAsc(String routeId);
}
