package com.example.cailanzi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_type")
public class ProductType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "类型代码不能为空")
    @Column(name = "type_code", nullable = false, unique = true)
    private String typeCode;

    @NotBlank(message = "类型名称不能为空")
    @Column(name = "type_name", nullable = false)
    private String typeName;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
