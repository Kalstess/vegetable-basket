package com.example.cailanzi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 企业自助注册请求：
 * 同时提交企业基本信息和企业管理员账号信息，待商务委审核。
 */
@Data
public class CompanyRegisterRequest {

    // 企业信息
    @NotBlank(message = "企业名称不能为空")
    private String companyName;

    private String address;

    private String legalPersonName;

    private String legalPersonPhone;

    private String freightPassContactName;

    private String freightPassContactPhone;

    private String companyType; // 国有/民营/其他

    private String businessScope;

    // 企业管理员账号信息
    @NotBlank(message = "管理员用户名不能为空")
    private String adminUsername;

    @NotBlank(message = "管理员密码不能为空")
    private String adminPassword;

    private String adminNickname;

    private String adminPhone;

    private String adminEmail;
}


