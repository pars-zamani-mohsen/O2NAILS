package com.o2nails.v11.ui.admin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.Animator;
import android.view.animation.DecelerateInterpolator;

import com.o2nails.v11.R;
import com.o2nails.v11.utils.AppConstants;
import com.o2nails.v11.utils.PreferenceManager;

public class PriceSettingsActivity extends Activity {

    private EditText priceEditText;
    private Button saveButton;
    private Button resetButton;
    private Button backButton;
    private TextView currentPriceTextView;
    private TextView statusTextView;

    private PreferenceManager preferenceManager;
    private int currentPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_settings);

        preferenceManager = new PreferenceManager(this);
        currentPrice = preferenceManager.getInt(AppConstants.PREF_PRINT_PRICE, AppConstants.DEFAULT_PRINT_PRICE);

        initializeViews();
        setupClickListeners();
        setupAnimations();
        updateUI();
    }

    private void initializeViews() {
        priceEditText = findViewById(R.id.priceEditText);
        saveButton = findViewById(R.id.saveButton);
        resetButton = findViewById(R.id.resetButton);
        backButton = findViewById(R.id.backButton);
        currentPriceTextView = findViewById(R.id.currentPriceTextView);
        statusTextView = findViewById(R.id.statusTextView);
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(saveButton);
                savePrice();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(resetButton);
                resetToDefault();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(backButton);
                finish();
            }
        });
    }

    private void setupAnimations() {
        // Initial entrance animation
        currentPriceTextView.setAlpha(0f);
        priceEditText.setAlpha(0f);
        saveButton.setAlpha(0f);
        resetButton.setAlpha(0f);

        ObjectAnimator currentFadeIn = ObjectAnimator.ofFloat(currentPriceTextView, "alpha", 0f, 1f);
        ObjectAnimator priceFadeIn = ObjectAnimator.ofFloat(priceEditText, "alpha", 0f, 1f);
        ObjectAnimator saveFadeIn = ObjectAnimator.ofFloat(saveButton, "alpha", 0f, 1f);
        ObjectAnimator resetFadeIn = ObjectAnimator.ofFloat(resetButton, "alpha", 0f, 1f);

        currentFadeIn.setDuration(600);
        priceFadeIn.setDuration(600);
        saveFadeIn.setDuration(600);
        resetFadeIn.setDuration(600);

        priceFadeIn.setStartDelay(200);
        saveFadeIn.setStartDelay(400);
        resetFadeIn.setStartDelay(600);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(currentFadeIn, priceFadeIn, saveFadeIn, resetFadeIn);
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

    private void updateUI() {
        currentPriceTextView.setText(String.format("قیمت فعلی: %s تومان", formatPrice(currentPrice)));
        priceEditText.setText(String.valueOf(currentPrice));
        statusTextView.setText("قیمت جدید را وارد کنید");
    }

    private String formatPrice(int price) {
        return String.format("%,d", price);
    }

    private void savePrice() {
        String priceText = priceEditText.getText().toString().trim();

        if (priceText.isEmpty()) {
            statusTextView.setText("لطفاً قیمت را وارد کنید");
            return;
        }

        try {
            int newPrice = Integer.parseInt(priceText);

            if (newPrice <= 0) {
                statusTextView.setText("قیمت باید بیشتر از صفر باشد");
                return;
            }

            if (newPrice > 100000) {
                statusTextView.setText("قیمت نباید بیشتر از 100,000 تومان باشد");
                return;
            }

            // Save new price
            preferenceManager.putInt(AppConstants.PREF_PRINT_PRICE, newPrice);
            currentPrice = newPrice;

            statusTextView.setText(getString(R.string.success_settings_saved));
            Toast.makeText(this, getString(R.string.success_settings_saved), Toast.LENGTH_SHORT).show();

            updateUI();

        } catch (NumberFormatException e) {
            statusTextView.setText("لطفاً یک عدد معتبر وارد کنید");
        }
    }

    private void resetToDefault() {
        preferenceManager.putInt(AppConstants.PREF_PRINT_PRICE, AppConstants.DEFAULT_PRINT_PRICE);
        currentPrice = AppConstants.DEFAULT_PRINT_PRICE;

        statusTextView.setText("قیمت به حالت پیش‌فرض بازنشانی شد");
        Toast.makeText(this, "قیمت به حالت پیش‌فرض بازنشانی شد", Toast.LENGTH_SHORT).show();

        updateUI();
    }
}
