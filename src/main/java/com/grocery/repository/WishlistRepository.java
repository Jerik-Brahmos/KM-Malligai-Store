package com.grocery.repository;

import com.grocery.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserId(String userId);

    @Query("SELECT COUNT(w) FROM WishlistItem w WHERE w.userId = :userId")
    int countByUserId(@Param("userId") String userId);

    List<WishlistItem> findByUserId(Long userId);

}
