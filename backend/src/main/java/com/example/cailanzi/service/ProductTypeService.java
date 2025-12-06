package com.example.cailanzi.service;

import com.example.cailanzi.entity.ProductType;
import com.example.cailanzi.repository.ProductTypeRepository;
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
public class ProductTypeService {

    private final ProductTypeRepository productTypeRepository;

    public List<ProductType> findAll() {
        return productTypeRepository.findAll();
    }

    public List<ProductType> findActiveTypes() {
        return productTypeRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    public Optional<ProductType> findById(Long id) {
        return productTypeRepository.findById(id);
    }

    public Optional<ProductType> findByTypeCode(String typeCode) {
        return productTypeRepository.findByTypeCode(typeCode);
    }

    public ProductType save(ProductType productType) {
        if (productTypeRepository.existsByTypeCode(productType.getTypeCode())) {
            throw new IllegalArgumentException("产品类型代码已存在: " + productType.getTypeCode());
        }
        return productTypeRepository.save(productType);
    }

    public ProductType update(Long id, ProductType productTypeDetails) {
        return productTypeRepository.findById(id)
                .map(productType -> {
                    if (!productType.getTypeCode().equals(productTypeDetails.getTypeCode()) &&
                            productTypeRepository.existsByTypeCode(productTypeDetails.getTypeCode())) {
                        throw new IllegalArgumentException("产品类型代码已存在: " + productTypeDetails.getTypeCode());
                    }

                    productType.setTypeCode(productTypeDetails.getTypeCode());
                    productType.setTypeName(productTypeDetails.getTypeName());
                    productType.setDisplayOrder(productTypeDetails.getDisplayOrder());
                    productType.setIsActive(productTypeDetails.getIsActive());

                    return productTypeRepository.save(productType);
                })
                .orElseThrow(() -> new IllegalArgumentException("产品类型不存在，ID: " + id));
    }

    public void deleteById(Long id) {
        if (!productTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("产品类型不存在，ID: " + id);
        }
        productTypeRepository.deleteById(id);
    }
}
