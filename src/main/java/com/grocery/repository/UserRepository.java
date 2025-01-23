package com.grocery.repository;

import com.grocery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUserId(String userId);
    List<User> findByUserIdContainingOrDisplayNameContainingOrEmailContaining(String userId, String displayName, String email);
    List<User> findByStatus(String status);
    List<User> findByRole(String role);

}
