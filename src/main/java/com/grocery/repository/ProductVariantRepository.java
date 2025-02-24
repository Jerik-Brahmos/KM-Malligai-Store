package com.grocery.repository;

import com.grocery.model.Product;
import com.grocery.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    // Find variants for a given product
    List<ProductVariant> findByProduct(Product product);

    @Transactional
    void deleteByProduct(Product product);

    List<ProductVariant> findByVariantIdIn(List<Long> variantIds);

    Optional<ProductVariant> findByVariantId(Long variantId);


}
