package com.o2nails.v11.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.o2nails.v11.R;
import com.o2nails.v11.utils.AppConstants;
import com.o2nails.v11.utils.CartManager;
import com.o2nails.v11.adapters.CartAdapter;
import com.o2nails.v11.models.CartItem;
import com.o2nails.v11.models.ImageItem;

import java.util.List;

public class CartManagementActivity extends Activity {

    private ListView cartListView;
    private TextView totalAmountTextView;
    private TextView totalItemsTextView;
    private Button proceedToPaymentButton;
    private Button backButton;
    private Button clearCartButton;

    private CartManager cartManager;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_management);

        cartManager = CartManager.getInstance();
        cartManager.setContext(this);
        initializeViews();
        setupCartList();
        setupClickListeners();
        updateCartSummary();
    }

    private void initializeViews() {
        cartListView = findViewById(R.id.cartListView);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        totalItemsTextView = findViewById(R.id.totalItemsTextView);
        proceedToPaymentButton = findViewById(R.id.proceedToPaymentButton);
        backButton = findViewById(R.id.backButton);
        clearCartButton = findViewById(R.id.clearCartButton);
    }

    private void setupCartList() {
        cartItems = cartManager.getCartItems();
        cartAdapter = new CartAdapter(this, cartItems, new CartAdapter.OnCartItemClickListener() {
            @Override
            public void onQuantityChanged(CartItem cartItem, int newQuantity) {
                cartManager.updateQuantity(cartItem, newQuantity);
                // Refresh the cart items list
                cartItems.clear();
                cartItems.addAll(cartManager.getCartItems());
                updateCartSummary();
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRemoveItem(CartItem cartItem) {
                cartManager.removeFromCart(cartItem);
                // Refresh the cart items list
                cartItems.clear();
                cartItems.addAll(cartManager.getCartItems());
                updateCartSummary();
                cartAdapter.notifyDataSetChanged();

                // Check if cart is empty
                if (cartManager.isEmpty()) {
                    Toast.makeText(CartManagementActivity.this,
                            "سبد خرید خالی شد", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        cartListView.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clearCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartManager.clearCart();
                updateCartSummary();
                cartAdapter.notifyDataSetChanged();
                Toast.makeText(CartManagementActivity.this,
                        "سبد خرید پاک شد", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        proceedToPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cartManager.isEmpty()) {
                    // For now, we'll go to the existing payment activity
                    // Later we can create a multi-item payment activity
                    proceedToPayment();
                } else {
                    Toast.makeText(CartManagementActivity.this,
                            "سبد خرید خالی است", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateCartSummary() {
        int totalItems = cartManager.getTotalItems();
        int totalAmount = cartManager.getTotalPrice();

        totalItemsTextView.setText("تعداد کل: " + totalItems);
        totalAmountTextView.setText("مبلغ کل: " + cartManager.getFormattedTotalPrice() + " تومان");

        proceedToPaymentButton.setEnabled(!cartManager.isEmpty());
    }

    private void proceedToPayment() {
        if (!cartManager.isEmpty()) {
            Intent intent = new Intent(this, PaymentActivity.class);
            // Send all cart items
            intent.putExtra(AppConstants.BUNDLE_CART_ITEMS, new java.util.ArrayList<>(cartManager.getCartItems()));
            intent.putExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, cartManager.getTotalPrice());
            intent.putExtra(AppConstants.BUNDLE_TOTAL_ITEMS, cartManager.getTotalItems());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update prices from preferences
        cartManager.updatePricesFromPreferences();
        // Refresh cart when returning to this activity
        cartItems.clear();
        cartItems.addAll(cartManager.getCartItems());
        cartAdapter.notifyDataSetChanged();
        updateCartSummary();
    }
}
