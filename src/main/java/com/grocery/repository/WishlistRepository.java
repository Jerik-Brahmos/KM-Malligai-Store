package com.grocery.repository;

import com.grocery.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserId(String userId);

    @Transactional(readOnly = true)
    @Query("SELECT COUNT(1) FROM WishlistItem w WHERE w.userId = :userId")
    int countByUserId(@Param("userId") String userId);


}
