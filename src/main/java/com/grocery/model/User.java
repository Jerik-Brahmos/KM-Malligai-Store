package com.grocery.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    private String userId;
    private String email;
    private String displayName;

    @Column(nullable = false)
    private String role = "customer"; // Default value for the role column

    @Column(nullable = false)
    private String status = "active"; // Default status is 'active'

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShippingAddress> shippingAddresses;



    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
