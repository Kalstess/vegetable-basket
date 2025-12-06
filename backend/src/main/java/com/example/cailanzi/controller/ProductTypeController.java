package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.entity.ProductType;
import com.example.cailanzi.service.ProductTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-types")
@RequiredArgsConstructor
@Tag(name = "产品类型管理", description = "产品类型字典管理API")
public class ProductTypeController {

    private final ProductTypeService productTypeService;

    @GetMapping
    @Operation(summary = "获取所有产品类型列表")
    public ResponseEntity<ResponseMessage<List<ProductType>>> getAllProductTypes() {
        return ResponseEntity.ok(ResponseMessage.success(productTypeService.findAll()));
    }

    @GetMapping("/active")
    @Operation(summary = "获取所有启用的产品类型列表")
    public ResponseEntity<ResponseMessage<List<ProductType>>> getActiveProductTypes() {
        return ResponseEntity.ok(ResponseMessage.success(productTypeService.findActiveTypes()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取产品类型详情")
    public ResponseEntity<ResponseMessage<ProductType>> getProductTypeById(@PathVariable Long id) {
        return productTypeService.findById(id)
                .map(dto -> ResponseEntity.ok(ResponseMessage.success(dto)))
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "产品类型不存在")));
    }

    @GetMapping("/code/{typeCode}")
    @Operation(summary = "根据类型代码获取产品类型")
    public ResponseEntity<ResponseMessage<ProductType>> getProductTypeByCode(@PathVariable String typeCode) {
        return productTypeService.findByTypeCode(typeCode)
                .map(dto -> ResponseEntity.ok(ResponseMessage.success(dto)))
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "产品类型不存在")));
    }

    @PostMapping
    @Operation(summary = "创建产品类型")
    public ResponseEntity<ResponseMessage<ProductType>> createProductType(@Valid @RequestBody ProductType productType) {
        ProductType saved = productTypeService.save(productType);
        return ResponseEntity.ok(ResponseMessage.success("产品类型创建成功", saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新产品类型")
    public ResponseEntity<ResponseMessage<ProductType>> updateProductType(
            @PathVariable Long id,
            @Valid @RequestBody ProductType productType) {
        ProductType updated = productTypeService.update(id, productType);
        return ResponseEntity.ok(ResponseMessage.success("产品类型更新成功", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除产品类型")
    public ResponseEntity<ResponseMessage<Void>> deleteProductType(@PathVariable Long id) {
        productTypeService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("产品类型删除成功", null));
    }
}
