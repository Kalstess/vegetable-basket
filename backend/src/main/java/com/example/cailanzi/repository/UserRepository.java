package com.example.cailanzi.repository;

import com.example.cailanzi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByCompanyId(Long companyId);
    List<User> findByVehicleId(Long vehicleId);
    List<User> findByRole(User.UserRole role);
}

