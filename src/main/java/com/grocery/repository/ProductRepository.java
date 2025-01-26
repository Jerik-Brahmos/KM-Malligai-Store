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

    @Query("SELECT p FROM Product p WHERE p.category = :categoryName AND p.isDeleted = false")
    List<Product> findByCategory(String categoryName);

    // Fetch top 'n' products for the homepage with limit
    @Query("SELECT p FROM Product p WHERE p.category = :categoryName AND p.isDeleted = false")
    List<Product> findTopByCategory(String categoryName, Pageable pageable);


    @Query(value = "SELECT * FROM product p " +
            "WHERE p.is_deleted = false " +
            "AND (p.name LIKE %:searchTerm% OR p.category LIKE %:searchTerm%)",
            countQuery = "SELECT COUNT(*) FROM product p " +
                    "WHERE p.is_deleted = false " +
                    "AND (p.name LIKE %:searchTerm% OR p.category LIKE %:searchTerm%)",
            nativeQuery = true)
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);


    @Query("UPDATE Product p SET p.isDeleted = true WHERE p.productId = :id")
    void softDeleteProduct(Long id);

    List<Product> findByIsDeletedFalse();

    @EntityGraph(attributePaths = {"category", "reviews"})
    @Query("SELECT p FROM Product p WHERE p.productId = :id AND p.isDeleted = false")
    Optional<Product> findByIdAndNotDeleted(Long id);

    Optional<Product> findByProductIdAndIsDeletedFalse(Long productId);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isDeleted = false")
    List<String> findAllCategories();


    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p WHERE p.category IN :categories AND p.isDeleted = false")
    List<Product> findByCategories(List<String> categories);


    @Query("SELECT p FROM Product p WHERE p.isDeleted = false")
    List<Product> findAllByIsDeletedFalse();


}
