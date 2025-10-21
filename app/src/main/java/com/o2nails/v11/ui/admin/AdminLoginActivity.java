package com.o2nails.v11.ui.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.Animator;
import android.view.animation.DecelerateInterpolator;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;

import com.o2nails.v11.R;
import com.o2nails.v11.utils.AppConstants;
import com.o2nails.v11.utils.PreferenceManager;

public class AdminLoginActivity extends Activity {

    private EditText passwordEditText;
    private Button loginButton;
    private Button cancelButton;
    private TextView statusTextView;
    private TextView lockCountdownTextView;
    private Button[] numberButtons;

    private PreferenceManager preferenceManager;
    private int failedAttempts = 0;
    private CountDownTimer lockTimer;
    private boolean isLocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        preferenceManager = new PreferenceManager(this);
        initializeViews();
        setupClickListeners();
        setupAnimations();
        checkLockStatus();
    }

    private void initializeViews() {
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        cancelButton = findViewById(R.id.cancelButton);
        statusTextView = findViewById(R.id.statusTextView);
        lockCountdownTextView = findViewById(R.id.lockCountdownTextView);

        // Initialize number buttons
        numberButtons = new Button[10];
        for (int i = 0; i < 10; i++) {
            int buttonId = getResources().getIdentifier("button" + i, "id", getPackageName());
            numberButtons[i] = findViewById(buttonId);
        }

        // Configure password field
        passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
        passwordEditText.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(8)});
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLocked) {
                    animateButtonClick(loginButton);
                    attemptLogin();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(cancelButton);
                finish();
            }
        });

        // Setup number button listeners
        for (int i = 0; i < numberButtons.length; i++) {
            final int number = i;
            numberButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isLocked) {
                        animateButtonClick((Button) v);
                        addDigitToPassword(String.valueOf(number));
                    }
                }
            });
        }
    }

    private void setupAnimations() {
        // Initial entrance animation
        passwordEditText.setAlpha(0f);
        loginButton.setAlpha(0f);
        cancelButton.setAlpha(0f);

        ObjectAnimator passwordFadeIn = ObjectAnimator.ofFloat(passwordEditText, "alpha", 0f, 1f);
        ObjectAnimator loginFadeIn = ObjectAnimator.ofFloat(loginButton, "alpha", 0f, 1f);
        ObjectAnimator cancelFadeIn = ObjectAnimator.ofFloat(cancelButton, "alpha", 0f, 1f);

        passwordFadeIn.setDuration(600);
        loginFadeIn.setDuration(600);
        cancelFadeIn.setDuration(600);

        loginFadeIn.setStartDelay(200);
        cancelFadeIn.setStartDelay(400);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(passwordFadeIn, loginFadeIn, cancelFadeIn);
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

    private void addDigitToPassword(String digit) {
        String currentPassword = passwordEditText.getText().toString();
        if (currentPassword.length() < 8) {
            passwordEditText.setText(currentPassword + digit);
        }
    }

    private void attemptLogin() {
        String enteredPassword = passwordEditText.getText().toString();

        if (enteredPassword.length() != 8) {
            statusTextView.setText("رمز عبور باید 8 رقم باشد");
            return;
        }

        // Get current admin password (either from preferences or default)
        String currentPassword = preferenceManager.getString("admin_password", AppConstants.ADMIN_PASSWORD);
        
        if (enteredPassword.equals(currentPassword)) {
            // Login successful
            onLoginSuccess();
        } else {
            // Login failed
            onLoginFailed();
        }
    }

    private void onLoginSuccess() {
        // Reset failed attempts
        preferenceManager.putInt(AppConstants.PREF_ADMIN_FAILED_ATTEMPTS, 0);
        preferenceManager.putLong(AppConstants.PREF_ADMIN_LOCK_TIME, 0);

        statusTextView.setText(getString(R.string.success_admin_login));
        Toast.makeText(this, getString(R.string.success_admin_login), Toast.LENGTH_SHORT).show();

        // Navigate to admin dashboard
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void onLoginFailed() {
        failedAttempts = preferenceManager.getInt(AppConstants.PREF_ADMIN_FAILED_ATTEMPTS, 0) + 1;
        preferenceManager.putInt(AppConstants.PREF_ADMIN_FAILED_ATTEMPTS, failedAttempts);

        int remainingAttempts = AppConstants.MAX_ADMIN_FAILED_ATTEMPTS - failedAttempts;

        if (remainingAttempts > 0) {
            statusTextView.setText(String.format("رمز عبور اشتباه است. %d تلاش باقی مانده", remainingAttempts));
            passwordEditText.setText("");
        } else {
            // Lock admin access
            lockAdminAccess();
        }
    }

    private void lockAdminAccess() {
        isLocked = true;
        long lockTime = System.currentTimeMillis();
        preferenceManager.putLong(AppConstants.PREF_ADMIN_LOCK_TIME, lockTime);

        statusTextView.setText(getString(R.string.error_admin_locked));
        lockCountdownTextView.setVisibility(View.VISIBLE);

        // Start countdown timer
        lockTimer = new CountDownTimer(AppConstants.ADMIN_LOCK_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / (1000 * 60));
                int seconds = (int) ((millisUntilFinished % (1000 * 60)) / 1000);
                lockCountdownTextView.setText(String.format("قفل شده: %d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                // Lock expired
                isLocked = false;
                failedAttempts = 0;
                preferenceManager.putInt(AppConstants.PREF_ADMIN_FAILED_ATTEMPTS, 0);
                preferenceManager.putLong(AppConstants.PREF_ADMIN_LOCK_TIME, 0);

                lockCountdownTextView.setVisibility(View.GONE);
                statusTextView.setText("رمز عبور را وارد کنید");
                passwordEditText.setText("");
            }
        };

        lockTimer.start();
    }

    private void checkLockStatus() {
        failedAttempts = preferenceManager.getInt(AppConstants.PREF_ADMIN_FAILED_ATTEMPTS, 0);
        long lockTime = preferenceManager.getLong(AppConstants.PREF_ADMIN_LOCK_TIME, 0);

        if (failedAttempts >= AppConstants.MAX_ADMIN_FAILED_ATTEMPTS && lockTime > 0) {
            long currentTime = System.currentTimeMillis();
            long remainingTime = (lockTime + AppConstants.ADMIN_LOCK_DURATION) - currentTime;

            if (remainingTime > 0) {
                // Still locked
                isLocked = true;
                lockCountdownTextView.setVisibility(View.VISIBLE);
                statusTextView.setText(getString(R.string.error_admin_locked));

                // Start countdown timer
                lockTimer = new CountDownTimer(remainingTime, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int minutes = (int) (millisUntilFinished / (1000 * 60));
                        int seconds = (int) ((millisUntilFinished % (1000 * 60)) / 1000);
                        lockCountdownTextView.setText(String.format("قفل شده: %d:%02d", minutes, seconds));
                    }

                    @Override
                    public void onFinish() {
                        // Lock expired
                        isLocked = false;
                        failedAttempts = 0;
                        preferenceManager.putInt(AppConstants.PREF_ADMIN_FAILED_ATTEMPTS, 0);
                        preferenceManager.putLong(AppConstants.PREF_ADMIN_LOCK_TIME, 0);

                        lockCountdownTextView.setVisibility(View.GONE);
                        statusTextView.setText("رمز عبور را وارد کنید");
                    }
                };

                lockTimer.start();
            } else {
                // Lock expired
                failedAttempts = 0;
                preferenceManager.putInt(AppConstants.PREF_ADMIN_FAILED_ATTEMPTS, 0);
                preferenceManager.putLong(AppConstants.PREF_ADMIN_LOCK_TIME, 0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lockTimer != null) {
            lockTimer.cancel();
        }
    }
}
