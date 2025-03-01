package com.grocery.repository;

import com.grocery.model.Product;
import com.grocery.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    // Find variants for a given product, excluding soft-deleted ones
    @Query("SELECT v FROM ProductVariant v WHERE v.product = :product AND v.isDeleted = false")
    List<ProductVariant> findByProduct(@Param("product") Product product);

    // Delete variants by product (no change needed, as it’s a deletion operation)
    @Transactional
    void deleteByProduct(Product product);

    // Find variants by IDs, excluding soft-deleted ones
    @Query("SELECT v FROM ProductVariant v WHERE v.variantId IN :variantIds AND v.isDeleted = false")
    List<ProductVariant> findByVariantIdIn(@Param("variantIds") List<Long> variantIds);

    // Find a variant by ID, excluding if soft-deleted
    @Query("SELECT v FROM ProductVariant v WHERE v.variantId = :variantId AND v.isDeleted = false")
    Optional<ProductVariant> findByVariantId(@Param("variantId") Long variantId);
}