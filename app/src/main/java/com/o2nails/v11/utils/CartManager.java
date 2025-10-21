package com.o2nails.v11.utils;

import android.content.Context;

import com.o2nails.v11.models.CartItem;
import com.o2nails.v11.models.ImageItem;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private List<CartItem> cartItems;
    private Context context;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void updatePricesFromPreferences() {
        if (context != null) {
            PreferenceManager preferenceManager = new PreferenceManager(context);
            int currentPrice = preferenceManager.getInt(AppConstants.PREF_PRINT_PRICE,
                    AppConstants.DEFAULT_PRINT_PRICE);

            // Update all cart items with current price
            for (CartItem item : cartItems) {
                item.setPricePerItem(currentPrice);
            }
        }
    }

    public void addToCart(ImageItem imageItem, int quantity) {
        CartItem newCartItem = new CartItem(imageItem, quantity);

        // Set price from preferences if context is available
        if (context != null) {
            PreferenceManager preferenceManager = new PreferenceManager(context);
            int currentPrice = preferenceManager.getInt(AppConstants.PREF_PRINT_PRICE,
                    AppConstants.DEFAULT_PRINT_PRICE);
            newCartItem.setPricePerItem(currentPrice);
        }

        // Check if item already exists in cart
        int existingIndex = findCartItemIndex(newCartItem);

        if (existingIndex >= 0) {
            // Update quantity of existing item
            CartItem existingItem = cartItems.get(existingIndex);
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Add new item to cart
            cartItems.add(newCartItem);
        }
    }

    public void removeFromCart(CartItem cartItem) {
        cartItems.remove(cartItem);
    }

    public void removeFromCart(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }

    public void updateQuantity(CartItem cartItem, int newQuantity) {
        if (newQuantity <= 0) {
            removeFromCart(cartItem);
        } else {
            cartItem.setQuantity(newQuantity);
        }
    }

    public void updateQuantity(int position, int newQuantity) {
        if (position >= 0 && position < cartItems.size()) {
            CartItem cartItem = cartItems.get(position);
            updateQuantity(cartItem, newQuantity);
        }
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public int getCartSize() {
        return cartItems.size();
    }

    public int getTotalItems() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    public int getTotalPrice() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public String getFormattedTotalPrice() {
        return String.format("%,d", getTotalPrice());
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public void clearCart() {
        cartItems.clear();
    }

    public boolean containsItem(ImageItem imageItem) {
        CartItem searchItem = new CartItem(imageItem, 1);
        return findCartItemIndex(searchItem) >= 0;
    }

    public int getItemQuantity(ImageItem imageItem) {
        CartItem searchItem = new CartItem(imageItem, 1);
        int index = findCartItemIndex(searchItem);
        if (index >= 0) {
            return cartItems.get(index).getQuantity();
        }
        return 0;
    }

    private int findCartItemIndex(CartItem searchItem) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).equals(searchItem)) {
                return i;
            }
        }
        return -1;
    }

    public CartItem getCartItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            return cartItems.get(position);
        }
        return null;
    }
}
