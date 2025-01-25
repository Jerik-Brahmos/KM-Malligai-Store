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


    @Query("SELECT p FROM Product p WHERE (p.name LIKE %:searchTerm% OR p.category LIKE %:searchTerm%) AND p.isDeleted = false")
    List<Product> searchProducts(String searchTerm);

    @Query("UPDATE Product p SET p.isDeleted = true WHERE p.productId = :id")
    void softDeleteProduct(Long id);

    List<Product> findByIsDeletedFalse();

    @Query("SELECT p FROM Product p WHERE p.productId = :id AND p.isDeleted = false")
    Optional<Product> findByIdAndNotDeleted(Long id);

    Optional<Product> findByProductIdAndIsDeletedFalse(Long productId);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isDeleted = false")
    List<String> findAllCategories();


    @Query("SELECT p FROM Product p WHERE p.category IN :categories AND p.isDeleted = false")
    List<Product> findByCategories(List<String> categories);

    List<Product> findAllByIsDeletedFalse();

}
