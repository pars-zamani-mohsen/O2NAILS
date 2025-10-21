package com.o2nails.v11.ui.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.Animator;
import android.view.animation.DecelerateInterpolator;

import com.o2nails.v11.R;
import com.o2nails.v11.utils.AppConstants;
import com.o2nails.v11.utils.PreferenceManager;

public class AdminDashboardActivity extends Activity {

    private Button priceSettingsButton;
    private Button imageManagementButton;
    private Button systemSettingsButton;
    private Button reportsButton;
    private Button passwordChangeButton;
    private Button logoutButton;
    private TextView welcomeTextView;
    private TextView statsTextView;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        preferenceManager = new PreferenceManager(this);
        initializeViews();
        setupClickListeners();
        setupAnimations();
        updateStats();
    }

    private void initializeViews() {
        priceSettingsButton = findViewById(R.id.priceSettingsButton);
        imageManagementButton = findViewById(R.id.imageManagementButton);
        systemSettingsButton = findViewById(R.id.systemSettingsButton);
        reportsButton = findViewById(R.id.reportsButton);
        passwordChangeButton = findViewById(R.id.passwordChangeButton);
        logoutButton = findViewById(R.id.logoutButton);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        statsTextView = findViewById(R.id.statsTextView);
    }

    private void setupClickListeners() {
        priceSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(priceSettingsButton);
                openPriceSettings();
            }
        });

        imageManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(imageManagementButton);
                openImageManagement();
            }
        });

        systemSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(systemSettingsButton);
                openSystemSettings();
            }
        });

        reportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(reportsButton);
                openReports();
            }
        });

        passwordChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(passwordChangeButton);
                openPasswordChange();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(logoutButton);
                logout();
            }
        });
    }

    private void setupAnimations() {
        // Initial entrance animation
        welcomeTextView.setAlpha(0f);
        priceSettingsButton.setAlpha(0f);
        imageManagementButton.setAlpha(0f);
        systemSettingsButton.setAlpha(0f);
        reportsButton.setAlpha(0f);
        passwordChangeButton.setAlpha(0f);
        logoutButton.setAlpha(0f);

        ObjectAnimator welcomeFadeIn = ObjectAnimator.ofFloat(welcomeTextView, "alpha", 0f, 1f);
        ObjectAnimator priceFadeIn = ObjectAnimator.ofFloat(priceSettingsButton, "alpha", 0f, 1f);
        ObjectAnimator imageFadeIn = ObjectAnimator.ofFloat(imageManagementButton, "alpha", 0f, 1f);
        ObjectAnimator systemFadeIn = ObjectAnimator.ofFloat(systemSettingsButton, "alpha", 0f, 1f);
        ObjectAnimator reportsFadeIn = ObjectAnimator.ofFloat(reportsButton, "alpha", 0f, 1f);
        ObjectAnimator passwordFadeIn = ObjectAnimator.ofFloat(passwordChangeButton, "alpha", 0f, 1f);
        ObjectAnimator logoutFadeIn = ObjectAnimator.ofFloat(logoutButton, "alpha", 0f, 1f);

        welcomeFadeIn.setDuration(600);
        priceFadeIn.setDuration(600);
        imageFadeIn.setDuration(600);
        systemFadeIn.setDuration(600);
        reportsFadeIn.setDuration(600);
        passwordFadeIn.setDuration(600);
        logoutFadeIn.setDuration(600);

        priceFadeIn.setStartDelay(200);
        imageFadeIn.setStartDelay(400);
        systemFadeIn.setStartDelay(600);
        reportsFadeIn.setStartDelay(800);
        passwordFadeIn.setStartDelay(1000);
        logoutFadeIn.setStartDelay(1200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(welcomeFadeIn, priceFadeIn, imageFadeIn, systemFadeIn, reportsFadeIn, passwordFadeIn, logoutFadeIn);
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

    private void updateStats() {
        // Get current price
        int currentPrice = preferenceManager.getInt(AppConstants.PREF_PRINT_PRICE, AppConstants.DEFAULT_PRINT_PRICE);

        // Get transaction history (simplified)
        String transactionHistory = preferenceManager.getString(AppConstants.PREF_TRANSACTION_HISTORY, "");
        int totalTransactions = transactionHistory.isEmpty() ? 0 : transactionHistory.split(",").length;

        // Update stats display
        String statsText = String.format("قیمت فعلی: %s تومان\nکل تراکنش‌ها: %d",
                formatPrice(currentPrice), totalTransactions);
        statsTextView.setText(statsText);
    }

    private String formatPrice(int price) {
        return String.format("%,d", price);
    }

    private void openPriceSettings() {
        Intent intent = new Intent(this, PriceSettingsActivity.class);
        startActivity(intent);
    }

    private void openImageManagement() {
        Intent intent = new Intent(this, ImageManagementActivity.class);
        startActivity(intent);
    }

    private void openSystemSettings() {
        Intent intent = new Intent(this, SystemSettingsActivity.class);
        startActivity(intent);
    }

    private void openReports() {
        Intent intent = new Intent(this, ReportsActivity.class);
        startActivity(intent);
    }

    private void openPasswordChange() {
        Intent intent = new Intent(this, PasswordChangeActivity.class);
        startActivity(intent);
    }

    private void logout() {
        Toast.makeText(this, "خروج از حساب مدیر", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }
}
