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

    List<Product> findByProductIdIn(List<Long> productIds);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.category = :categoryName AND p.isDeleted = false")
    List<Product> findTopByCategoryWithVariants(@Param("categoryName") String categoryName, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.category = :categoryName AND p.isDeleted = false")
    List<Product> findByCategoryWithVariants(@Param("categoryName") String categoryName);


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
        AND MATCH(p.name, p.category) AGAINST(:searchTerm IN BOOLEAN MODE)
        """,
            nativeQuery = true)
    List<Object[]> searchProductsWithVariants(@Param("searchTerm") String searchTerm);

    @Query("UPDATE Product p SET p.isDeleted = true WHERE p.productId = :id")
    void softDeleteProduct(Long id);

    List<Product> findByIsDeletedFalse();

    @EntityGraph(attributePaths = {"category", "reviews"})
    @Query("SELECT p FROM Product p WHERE p.productId = :id AND p.isDeleted = false")
    Optional<Product> findByIdAndNotDeleted(Long id);

    Optional<Product> findByProductIdAndIsDeletedFalse(Long productId);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isDeleted = false")
    List<String> findAllCategories();


    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.category IN :categories AND p.isDeleted = false")
    List<Product> findByCategoriesWithVariants(@Param("categories") List<String> categories);



//    @Query("SELECT p FROM Product p WHERE p.isDeleted = false")
//    List<Product> findAllByIsDeletedFalse();


    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.isDeleted = false")
    List<Product> findAllByIsDeletedFalse();





}
