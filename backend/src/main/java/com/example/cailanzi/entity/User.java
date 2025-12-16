package com.example.cailanzi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 登录密码，使用 BCrypt 进行加密存储。
     * 兼容旧数据时，会在登录流程里自动迁移为 BCrypt。
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private UserRole role = UserRole.COMPANY_USER;

    /**
     * 企业主归属（历史字段，仍然保留，兼容现有业务逻辑）。
     * 对于企业管理员/企业普通用户/司机用户，一般都会有一个“主企业”。
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Company company;

    /**
     * 司机用户关联的车辆ID（司机负责的车辆）
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "company"})
    private Vehicle vehicle;

    /**
     * 支持用户归属多个企业，用于顾问类账号或后续扩展。
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_company_rel",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Company> companies = new HashSet<>();

    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 微信相关预留字段，用于小程序/公众号登录打通。
     */
    @Column(name = "wechat_open_id", length = 64)
    private String wechatOpenId;

    @Column(name = "wechat_union_id", length = 64)
    private String wechatUnionId;

    /**
     * 登录安全与审计信息
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip", length = 64)
    private String lastLoginIp;

    @Column(name = "login_fail_count")
    private Integer loginFailCount = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    public enum UserRole {
        /**
         * 系统超级管理员：拥有全局配置与用户管理权限。
         */
        ADMIN,

        /**
         * 商务委用户：侧重数据查看、分析与企业/账号审批，不直接做系统运维。
         */
        BUSINESS_COMMISSION,

        /**
         * 历史角色：企业用户（默认视作企业管理员使用）。
         * 为兼容旧数据保留，新的业务中建议使用 COMPANY_ADMIN / COMPANY_USER。
         */
        COMPANY,

        /**
         * 企业管理员：管理本企业用户、车辆等资源。
         */
        COMPANY_ADMIN,

        /**
         * 企业普通用户：只能查看/维护本企业业务数据，无用户管理权限。
         */
        COMPANY_USER,

        /**
         * 司机用户：与具体车辆绑定，主要用于运行数据填报。
         */
        DRIVER
    }
}


