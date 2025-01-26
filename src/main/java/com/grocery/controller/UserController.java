package com.grocery.controller;

import com.grocery.dto.UserItems;
import com.grocery.model.User;
import com.grocery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://grocery-shop-ee0ac.web.app", "https://grocery-shop-ee0ac.firebaseapp.com"})
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User saveUser(@RequestBody User user) {
        System.out.println("User details to save: "+user);
        return userService.saveUser(user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("/search")
    public List<User> searchUsers(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role) {
        return userService.searchUsers(searchTerm, status, role);
    }



    @PutMapping("/{userId}")
    public User updateUser(@PathVariable String userId, @RequestBody User user) {
        return userService.updateUser(userId, user);
    }


    // Endpoint to fetch user role
    @GetMapping("/role")
    public String getUserRole(@RequestParam String userId) {
        System.out.println("userID to check:" + userId);
        User user = userService.findUserById(userId);
        if (user != null) {
            return user.getRole();
        }
        throw new RuntimeException("User not found with ID: " + userId);
    }


        @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        long count = userService.getUserCount();
        return ResponseEntity.ok(count);
    }


    @GetMapping("/items")
    public ResponseEntity<UserItems> getUserItems(@RequestParam String userId) {
        UserItems userItems = userService.getUserItems(userId);
        return ResponseEntity.ok(userItems);
    }

}
