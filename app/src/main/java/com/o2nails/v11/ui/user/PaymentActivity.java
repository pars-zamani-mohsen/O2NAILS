package com.o2nails.v11.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.Animator;
import android.view.animation.DecelerateInterpolator;

import com.o2nails.v11.R;
import com.o2nails.v11.utils.AppConstants;
import com.o2nails.v11.utils.CartManager;
import com.o2nails.v11.models.ImageItem;
import com.o2nails.v11.models.CartItem;
import com.o2nails.v11.service.PaymentService;

import java.util.List;

public class PaymentActivity extends Activity implements PaymentService.PaymentCallback {

    private ImageView previewImageView;
    private TextView quantityTextView;
    private TextView totalAmountTextView;
    private Button cardPaymentButton;
    private Button backButton;
    private TextView statusTextView;

    private List<CartItem> cartItems;
    private int totalAmount;
    private int totalItems;
    private PaymentService paymentService;
    private boolean paymentInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paymentService = new PaymentService(this);
        paymentService.setPaymentCallback(this);

        initializeViews();
        loadData();
        setupClickListeners();
        setupAnimations();
    }

    private void initializeViews() {
        previewImageView = findViewById(R.id.previewImageView);
        quantityTextView = findViewById(R.id.quantityTextView);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        cardPaymentButton = findViewById(R.id.cardPaymentButton);
        backButton = findViewById(R.id.backButton);
        statusTextView = findViewById(R.id.statusTextView);
    }

    private void loadData() {
        // Try to get cart items first (new multi-item flow)
        cartItems = (List<CartItem>) getIntent().getSerializableExtra(AppConstants.BUNDLE_CART_ITEMS);

        if (cartItems != null && !cartItems.isEmpty()) {
            // Multi-item payment flow
            totalAmount = getIntent().getIntExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, 0);
            totalItems = getIntent().getIntExtra(AppConstants.BUNDLE_TOTAL_ITEMS, 0);

            updateUI();
        } else {
            // Fallback to single item flow (for backward compatibility)
            ImageItem selectedImage = (ImageItem) getIntent().getSerializableExtra(AppConstants.BUNDLE_SELECTED_IMAGE);
            int printQuantity = getIntent().getIntExtra(AppConstants.BUNDLE_PRINT_QUANTITY, 1);
            totalAmount = getIntent().getIntExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, 0);

            if (selectedImage == null || totalAmount == 0) {
                Toast.makeText(this, "خطا در بارگذاری اطلاعات", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            updateUI();
        }
    }

    private void updateUI() {
        if (cartItems != null && !cartItems.isEmpty()) {
            // Multi-item flow
            // Display first image as preview
            ImageItem firstImage = cartItems.get(0).getImageItem();
            if (firstImage.getType() == ImageItem.TYPE_DEFAULT) {
                previewImageView.setImageResource(firstImage.getResourceId());
            } else {
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(firstImage.getFilePath());
                if (bitmap != null) {
                    previewImageView.setImageBitmap(bitmap);
                }
            }

            quantityTextView.setText(String.format("تعداد کل: %d", totalItems));
            totalAmountTextView.setText(String.format("مبلغ کل: %s تومان", formatPrice(totalAmount)));
            statusTextView.setText("روش پرداخت را انتخاب کنید");
        } else {
            // Single item flow (fallback)
            ImageItem selectedImage = (ImageItem) getIntent().getSerializableExtra(AppConstants.BUNDLE_SELECTED_IMAGE);
            int printQuantity = getIntent().getIntExtra(AppConstants.BUNDLE_PRINT_QUANTITY, 1);

            if (selectedImage.getType() == ImageItem.TYPE_DEFAULT) {
                previewImageView.setImageResource(selectedImage.getResourceId());
            } else {
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(selectedImage.getFilePath());
                if (bitmap != null) {
                    previewImageView.setImageBitmap(bitmap);
                }
            }

            quantityTextView.setText(String.format("تعداد: %d", printQuantity));
            totalAmountTextView.setText(String.format("مبلغ کل: %s تومان", formatPrice(totalAmount)));
            statusTextView.setText("روش پرداخت را انتخاب کنید");
        }
    }

    private String formatPrice(int price) {
        return String.format("%,d", price);
    }

    private void setupClickListeners() {
        cardPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!paymentInProgress) {
                    animateButtonClick(cardPaymentButton);
                    processCardPayment();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!paymentInProgress) {
                    animateButtonClick(backButton);
                    finish();
                }
            }
        });
    }

    private void setupAnimations() {
        // Initial entrance animation
        previewImageView.setAlpha(0f);
        cardPaymentButton.setAlpha(0f);

        ObjectAnimator previewFadeIn = ObjectAnimator.ofFloat(previewImageView, "alpha", 0f, 1f);
        ObjectAnimator cardFadeIn = ObjectAnimator.ofFloat(cardPaymentButton, "alpha", 0f, 1f);

        previewFadeIn.setDuration(600);
        cardFadeIn.setDuration(600);

        cardFadeIn.setStartDelay(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(previewFadeIn, cardFadeIn);
        animatorSet.start();
    }

    private void animateButtonClick(Button button) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(150);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    private void processCardPayment() {
        paymentInProgress = true;
        statusTextView.setText("در حال اتصال به دستگاه POS...");
        updateButtonStates(false);

        // Simulate POS connection and payment processing
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                paymentService.processCardPayment(totalAmount);
            }
        }, 2000);
    }

    private void updateButtonStates(boolean enabled) {
        cardPaymentButton.setEnabled(enabled);
        backButton.setEnabled(enabled);
    }

    @Override
    public void onPaymentSuccess(String transactionId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(getString(R.string.success_payment));
                Toast.makeText(PaymentActivity.this, getString(R.string.success_payment), Toast.LENGTH_SHORT).show();

                // Proceed to printing
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proceedToPrinting(transactionId);
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onPaymentFailed(String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                paymentInProgress = false;
                statusTextView.setText("پرداخت ناموفق: " + errorMessage);
                updateButtonStates(true);
                Toast.makeText(PaymentActivity.this, getString(R.string.error_payment_failed), Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public void onPaymentProgress(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(message);
            }
        });
    }

    private void proceedToPrinting(String transactionId) {
        Intent intent = new Intent(this, PrintingActivity.class);

        if (cartItems != null && !cartItems.isEmpty()) {
            // Multi-item flow
            intent.putExtra(AppConstants.BUNDLE_CART_ITEMS, new java.util.ArrayList<>(cartItems));
            intent.putExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, totalAmount);
            intent.putExtra(AppConstants.BUNDLE_TOTAL_ITEMS, totalItems);
        } else {
            // Single item flow (fallback)
            ImageItem selectedImage = (ImageItem) getIntent().getSerializableExtra(AppConstants.BUNDLE_SELECTED_IMAGE);
            int printQuantity = getIntent().getIntExtra(AppConstants.BUNDLE_PRINT_QUANTITY, 1);
            intent.putExtra(AppConstants.BUNDLE_SELECTED_IMAGE, selectedImage);
            intent.putExtra(AppConstants.BUNDLE_PRINT_QUANTITY, printQuantity);
            intent.putExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, totalAmount);
        }

        intent.putExtra(AppConstants.BUNDLE_TRANSACTION_ID, transactionId);
        intent.putExtra(AppConstants.BUNDLE_PAYMENT_SUCCESS, true);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (paymentService != null) {
            paymentService.cleanup();
        }
    }
}
