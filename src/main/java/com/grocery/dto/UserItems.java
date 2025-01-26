package com.grocery.dto;

import com.grocery.model.CartItem;
import com.grocery.model.WishlistItem;

import java.util.List;

public class UserItems {
    private List<WishlistItem> wishlist;
    private List<CartItem> cart;

    public UserItems(List<WishlistItem> wishlist, List<CartItem> cart) {
        this.wishlist = wishlist;
        this.cart = cart;
    }

    public List<WishlistItem> getWishlist() {
        return wishlist;
    }

    public List<CartItem> getCart() {
        return cart;
    }
}
