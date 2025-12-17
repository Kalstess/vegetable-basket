package com.example.cailanzi.repository;

import com.example.cailanzi.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByName(String name);

    List<Company> findByCompanyType(Company.CompanyType companyType);

    boolean existsByName(String name);

    @Query("SELECT c FROM Company c WHERE c.name LIKE %:keyword% OR c.businessScope LIKE %:keyword%")
    List<Company> searchByKeyword(String keyword);

    List<Company> findByStatus(Company.CompanyStatus status);
}