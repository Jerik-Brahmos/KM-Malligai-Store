package com.grocery.repository;

import com.grocery.model.Product;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Fetch products by IDs, excluding soft-deleted variants
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants v WHERE p.productId IN :productIds AND p.isDeleted = false AND (v.isDeleted = false OR v IS NULL)")
    List<Product> findByProductIdIn(@Param("productIds") List<Long> productIds);

    // Fetch top products by category, excluding soft-deleted variants
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants v WHERE p.category = :categoryName AND p.isDeleted = false AND (v.isDeleted = false OR v IS NULL)")
    List<Product> findTopByCategoryWithVariants(@Param("categoryName") String categoryName, Pageable pageable);

    // Fetch products by category, excluding soft-deleted variants
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants v WHERE p.category = :categoryName AND p.isDeleted = false AND (v.isDeleted = false OR v IS NULL)")
    List<Product> findByCategoryWithVariants(@Param("categoryName") String categoryName);

    // Native query for search, excluding soft-deleted variants
    @Query(value = """
        SELECT
            p.product_id AS productId,
            p.name,
            p.category,
            p.image_url AS imageUrl,
            v.variant_id AS variantId,
            v.grams,
            v.price
        FROM product p
        LEFT JOIN product_variant v ON p.product_id = v.product_id
        WHERE p.is_deleted = false
        AND (v.is_deleted = false OR v.variant_id IS NULL)
        AND MATCH(p.name, p.category) AGAINST(:searchTerm IN BOOLEAN MODE)
        """,
            nativeQuery = true)
    List<Object[]> searchProductsWithVariants(@Param("searchTerm") String searchTerm);

    // Soft delete a product (no variant fetching, so no change needed)
    @Query("UPDATE Product p SET p.isDeleted = true WHERE p.productId = :id")
    void softDeleteProduct(@Param("id") Long id);

    // Fetch all non-deleted products, excluding soft-deleted variants
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants v WHERE p.isDeleted = false AND (v.isDeleted = false OR v IS NULL)")
    List<Product> findByIsDeletedFalse();

    // Fetch a single product by ID, excluding soft-deleted variants
    @EntityGraph(attributePaths = {"category", "variants"})
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.variants v WHERE p.productId = :id AND p.isDeleted = false AND (v.isDeleted = false OR v IS NULL)")
    Optional<Product> findByIdAndNotDeleted(@Param("id") Long id);

    // Fetch a single product by ID with isDeleted check, excluding soft-deleted variants
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.variants v WHERE p.productId = :productId AND p.isDeleted = false AND (v.isDeleted = false OR v IS NULL)")
    Optional<Product> findByProductIdAndIsDeletedFalse(@Param("productId") Long productId);

    // Fetch all distinct categories (no variants involved, so no change needed)
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isDeleted = false")
    List<String> findAllCategories();

    // Fetch products by multiple categories, excluding soft-deleted variants
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants v WHERE p.category IN :categories AND p.isDeleted = false AND (v.isDeleted = false OR v IS NULL)")
    List<Product> findByCategoriesWithVariants(@Param("categories") List<String> categories);

    // Fetch all products, excluding soft-deleted variants (already correct in your last version)
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants v WHERE p.isDeleted = false AND (v.isDeleted = false OR v IS NULL)")
    List<Product> findAllByIsDeletedFalse();
}