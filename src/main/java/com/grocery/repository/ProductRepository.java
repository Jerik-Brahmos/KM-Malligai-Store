package com.grocery.repository;

import com.grocery.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.category = :categoryName AND p.isDeleted = false")
    List<Product> findByCategory(String categoryName);

    // Custom query for searching products excluding deleted ones
    @Query("SELECT p FROM Product p WHERE (p.name LIKE %:searchTerm% OR p.category LIKE %:searchTerm%) AND p.isDeleted = false")
    List<Product> searchProducts(String searchTerm);

    // Soft delete product by setting deleted flag to true
    @Query("UPDATE Product p SET p.isDeleted = true WHERE p.productId = :id")
    void softDeleteProduct(Long id);

    // Optional: You can also create a method for fetching all non-deleted products
    List<Product> findByIsDeletedFalse();

    @Query("SELECT p FROM Product p WHERE p.productId = :id AND p.isDeleted = false")
    Optional<Product> findByIdAndNotDeleted(Long id);

    Optional<Product> findByProductIdAndIsDeletedFalse(Long productId);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isDeleted = false")
    List<String> findAllCategories();

    @Query("SELECT p FROM Product p WHERE p.category IN :categories AND p.isDeleted = false")
    List<Product> findByCategories(List<String> categories);



}
