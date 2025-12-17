package com.example.cailanzi.service;

import com.example.cailanzi.entity.Company;
import com.example.cailanzi.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    public Company save(Company company) {
        // 仅在新建企业时检查名称重复，更新时由 update 方法负责控制
        if (company.getId() == null && companyRepository.existsByName(company.getName())) {
            throw new IllegalArgumentException("企业名称已存在: " + company.getName());
        }
        return companyRepository.save(company);
    }

    public Company update(Long id, Company companyDetails) {
        return companyRepository.findById(id)
                .map(company -> {
                    if (!company.getName().equals(companyDetails.getName()) &&
                            companyRepository.existsByName(companyDetails.getName())) {
                        throw new IllegalArgumentException("企业名称已存在: " + companyDetails.getName());
                    }

                    company.setName(companyDetails.getName());
                    company.setEstablishedDate(companyDetails.getEstablishedDate());
                    company.setAddress(companyDetails.getAddress());
                    company.setLegalPersonName(companyDetails.getLegalPersonName());
                    company.setLegalPersonPhone(companyDetails.getLegalPersonPhone());
                    company.setFreightPassContactName(companyDetails.getFreightPassContactName());
                    company.setFreightPassContactPhone(companyDetails.getFreightPassContactPhone());
                    company.setCompanyType(companyDetails.getCompanyType());
                    company.setRegisteredCapital(companyDetails.getRegisteredCapital());
                    company.setRevenue2022(companyDetails.getRevenue2022());
                    company.setRevenue2023(companyDetails.getRevenue2023());
                    company.setRevenue2024(companyDetails.getRevenue2024());
                    company.setBusinessScope(companyDetails.getBusinessScope());

                    return companyRepository.save(company);
                })
                .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + id));
    }

    public void deleteById(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new IllegalArgumentException("企业不存在，ID: " + id);
        }
        companyRepository.deleteById(id);
    }

    public List<Company> searchByKeyword(String keyword) {
        return companyRepository.searchByKeyword(keyword);
    }

    public List<Company> findByStatus(Company.CompanyStatus status) {
        return companyRepository.findByStatus(status);
    }
}