package com.grocery.service;

import com.grocery.dto.UserItems;
import com.grocery.model.CartItem;
import com.grocery.model.User;
import com.grocery.model.WishlistItem;
import com.grocery.repository.CartRepository;
import com.grocery.repository.UserRepository;
import com.grocery.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private CartRepository cartRepository;

    public User saveUser(User user) {
        System.out.println("Saving user: " + user);
        User existingUser = userRepository.findByUserId(user.getUserId());
        if (existingUser != null) {
            System.out.println("User already exists: " + existingUser);
            return existingUser;
        }
        User savedUser = userRepository.save(user);
        System.out.println("User saved: " + savedUser);
        return savedUser;
    }


    public User findUserById(String userId) {
        return userRepository.findByUserId(userId);
    }

    public List<User> searchUsers(String searchTerm, String status, String role) {
        if (role != null && !role.isEmpty()) {
            return userRepository.findByRole(role);
        }
        if (status != null && !status.isEmpty()) {
            return userRepository.findByStatus(status);
        }
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return userRepository.findByUserIdContainingOrDisplayNameContainingOrEmailContaining(searchTerm, searchTerm, searchTerm);
        }
        return userRepository.findAll();
    }



    public User updateUser(String userId, User user) {
        User existingUser = userRepository.findByUserId(userId);
        if (existingUser != null) {
            existingUser.setDisplayName(user.getDisplayName());
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());
            existingUser.setStatus(user.getStatus());
            return userRepository.save(existingUser);
        }
        return null;
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public UserItems getUserItems(String userId) {
        List<WishlistItem> wishlistItems = wishlistRepository.findByUserId(userId);
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        return new UserItems(wishlistItems, cartItems);
    }


}
