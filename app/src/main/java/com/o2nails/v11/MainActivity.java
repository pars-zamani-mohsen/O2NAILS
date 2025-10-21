package com.o2nails.v11;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.Animator;
import android.view.animation.DecelerateInterpolator;

import com.o2nails.v11.ui.user.ImageSelectionActivity;
import com.o2nails.v11.ui.admin.AdminLoginActivity;
import com.o2nails.v11.utils.AppConstants;
import com.o2nails.v11.utils.PreferenceManager;

public class MainActivity extends Activity {

    private Button startButton;
    private Button adminButton;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(this);
        initializeViews();
        setupClickListeners();
        setupAnimations();

        // Check if admin mode is locked
        checkAdminLockStatus();
    }

    private void initializeViews() {
        startButton = findViewById(R.id.startButton);
        adminButton = findViewById(R.id.adminButton);
    }

    private void setupClickListeners() {
        // Start Button - Navigate to image selection
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(startButton);
                Intent intent = new Intent(MainActivity.this, ImageSelectionActivity.class);
                startActivity(intent);
            }
        });

        // Admin Button - Check lock status and navigate
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(adminButton);

                if (isAdminLocked()) {
                    long lockTime = preferenceManager.getLong(AppConstants.PREF_ADMIN_LOCK_TIME, 0);
                    long currentTime = System.currentTimeMillis();
                    long remainingTime = (lockTime + AppConstants.ADMIN_LOCK_DURATION) - currentTime;

                    if (remainingTime > 0) {
                        int minutes = (int) (remainingTime / (1000 * 60));
                        int seconds = (int) ((remainingTime % (1000 * 60)) / 1000);
                        Toast.makeText(MainActivity.this,
                                String.format("دسترسی قفل شده است. %d:%02d باقی مانده", minutes, seconds),
                                Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        // Lock expired, reset failed attempts
                        preferenceManager.putInt(AppConstants.PREF_ADMIN_FAILED_ATTEMPTS, 0);
                        preferenceManager.putLong(AppConstants.PREF_ADMIN_LOCK_TIME, 0);
                    }
                }

                Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupAnimations() {
        // Initial entrance animation for buttons
        startButton.setAlpha(0f);
        adminButton.setAlpha(0f);

        ObjectAnimator startFadeIn = ObjectAnimator.ofFloat(startButton, "alpha", 0f, 1f);
        ObjectAnimator adminFadeIn = ObjectAnimator.ofFloat(adminButton, "alpha", 0f, 1f);

        startFadeIn.setDuration(800);
        adminFadeIn.setDuration(800);
        adminFadeIn.setStartDelay(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(startFadeIn, adminFadeIn);
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

    private void checkAdminLockStatus() {
        if (isAdminLocked()) {
            long lockTime = preferenceManager.getLong(AppConstants.PREF_ADMIN_LOCK_TIME, 0);
            long currentTime = System.currentTimeMillis();

            if (currentTime - lockTime >= AppConstants.ADMIN_LOCK_DURATION) {
                // Lock expired, reset
                preferenceManager.putInt(AppConstants.PREF_ADMIN_FAILED_ATTEMPTS, 0);
                preferenceManager.putLong(AppConstants.PREF_ADMIN_LOCK_TIME, 0);
            }
        }
    }

    private boolean isAdminLocked() {
        int failedAttempts = preferenceManager.getInt(AppConstants.PREF_ADMIN_FAILED_ATTEMPTS, 0);
        return failedAttempts >= AppConstants.MAX_ADMIN_FAILED_ATTEMPTS;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAdminLockStatus();
    }
}
