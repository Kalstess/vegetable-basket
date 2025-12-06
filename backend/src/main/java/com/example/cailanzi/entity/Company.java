package com.example.cailanzi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "company")
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "企业名称不能为空")
    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "established_date")
    private LocalDate establishedDate;

    private String address;

    @Column(name = "legal_person_name")
    private String legalPersonName;

    @Column(name = "legal_person_phone")
    private String legalPersonPhone;

    @Column(name = "freight_pass_contact_name")
    private String freightPassContactName;

    @Column(name = "freight_pass_contact_phone")
    private String freightPassContactPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type")
    private CompanyType companyType;

    @Column(name = "registered_capital", precision = 12, scale = 2)
    private BigDecimal registeredCapital;

    @Column(name = "revenue_2022", precision = 14, scale = 2)
    private BigDecimal revenue2022;

    @Column(name = "revenue_2023", precision = 14, scale = 2)
    private BigDecimal revenue2023;

    @Column(name = "revenue_2024", precision = 14, scale = 2)
    private BigDecimal revenue2024;

    @Column(name = "business_scope", columnDefinition = "TEXT")
    private String businessScope;

    public enum CompanyType {
        国有, 民营, 其他
    }
}