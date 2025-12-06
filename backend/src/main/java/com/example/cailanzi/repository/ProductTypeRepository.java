package com.example.cailanzi.repository;

import com.example.cailanzi.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {

    Optional<ProductType> findByTypeCode(String typeCode);

    List<ProductType> findByIsActiveTrueOrderByDisplayOrderAsc();

    boolean existsByTypeCode(String typeCode);
}
