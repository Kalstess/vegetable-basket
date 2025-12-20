package com.example.cailanzi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "route_point",
        uniqueConstraints = @UniqueConstraint(columnNames = {"route_id", "seq"}))
public class RoutePoint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull(message = "路线日期不能为空")
    @Column(name = "route_date", nullable = false)
    private LocalDate routeDate;

    @NotBlank(message = "路线标识不能为空")
    @Column(name = "route_id", nullable = false)
    private String routeId;

    @NotNull(message = "到达顺序不能为空")
    @Column(nullable = false)
    private Integer seq;

    @NotBlank(message = "地点地址不能为空")
    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type")
    private PointType pointType;

    @Column(name = "arrive_time")
    private LocalDateTime arriveTime;

    @Column(name = "depart_time")
    private LocalDateTime departTime;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public enum PointType {
        批发市场, 配送中心, 超市卖场便利店, 餐饮店, 商场, 菜场, 其他
    }
}
