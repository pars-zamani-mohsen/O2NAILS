package com.o2nails.v11.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.o2nails.v11.utils.PreferenceManager;
import com.o2nails.v11.models.ImageItem;

public class PrintQuantityActivity extends Activity {

    private ImageView previewImageView;
    private TextView quantityTextView;
    private TextView pricePerPrintTextView;
    private TextView totalAmountTextView;
    private Button decreaseButton;
    private Button increaseButton;
    private Button nextButton;
    private Button backButton;

    private ImageItem selectedImage;
    private int printQuantity = 1;
    private int pricePerPrint;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_quantity);

        preferenceManager = new PreferenceManager(this);
        pricePerPrint = preferenceManager.getInt(AppConstants.PREF_PRINT_PRICE, AppConstants.DEFAULT_PRINT_PRICE);

        initializeViews();
        loadSelectedImage();
        setupClickListeners();
        updateUI();
        setupAnimations();
    }

    private void initializeViews() {
        previewImageView = findViewById(R.id.previewImageView);
        quantityTextView = findViewById(R.id.quantityTextView);
        pricePerPrintTextView = findViewById(R.id.pricePerPrintTextView);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        decreaseButton = findViewById(R.id.decreaseButton);
        increaseButton = findViewById(R.id.increaseButton);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);
    }

    private void loadSelectedImage() {
        selectedImage = (ImageItem) getIntent().getSerializableExtra(AppConstants.BUNDLE_SELECTED_IMAGE);
        if (selectedImage == null) {
            Toast.makeText(this, getString(R.string.error_no_image_selected), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displaySelectedImage();
    }

    private void displaySelectedImage() {
        if (selectedImage.getType() == ImageItem.TYPE_DEFAULT) {
            previewImageView.setImageResource(selectedImage.getResourceId());
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImage.getFilePath());
            if (bitmap != null) {
                previewImageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, getString(R.string.error_image_load), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupClickListeners() {
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(decreaseButton);
                if (printQuantity > 1) {
                    printQuantity--;
                    updateUI();
                }
            }
        });

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(increaseButton);
                if (printQuantity < 10) { // Maximum 10 prints
                    printQuantity++;
                    updateUI();
                } else {
                    Toast.makeText(PrintQuantityActivity.this, "حداکثر 10 پرینت مجاز است", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(nextButton);
                proceedToPayment();
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

    private void updateUI() {
        quantityTextView.setText(String.valueOf(printQuantity));
        pricePerPrintTextView.setText(String.format("%s: %s تومان",
                getString(R.string.price_per_print), formatPrice(pricePerPrint)));

        int totalAmount = printQuantity * pricePerPrint;
        totalAmountTextView.setText(String.format("%s: %s تومان",
                getString(R.string.total_amount), formatPrice(totalAmount)));

        // Update button states
        decreaseButton.setEnabled(printQuantity > 1);
        increaseButton.setEnabled(printQuantity < 10);
    }

    private String formatPrice(int price) {
        return String.format("%,d", price);
    }

    private void setupAnimations() {
        // Initial entrance animation
        previewImageView.setAlpha(0f);
        quantityTextView.setAlpha(0f);
        totalAmountTextView.setAlpha(0f);

        ObjectAnimator previewFadeIn = ObjectAnimator.ofFloat(previewImageView, "alpha", 0f, 1f);
        ObjectAnimator quantityFadeIn = ObjectAnimator.ofFloat(quantityTextView, "alpha", 0f, 1f);
        ObjectAnimator totalFadeIn = ObjectAnimator.ofFloat(totalAmountTextView, "alpha", 0f, 1f);

        previewFadeIn.setDuration(600);
        quantityFadeIn.setDuration(600);
        totalFadeIn.setDuration(600);

        quantityFadeIn.setStartDelay(200);
        totalFadeIn.setStartDelay(400);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(previewFadeIn, quantityFadeIn, totalFadeIn);
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

    private void proceedToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(AppConstants.BUNDLE_SELECTED_IMAGE, selectedImage);
        intent.putExtra(AppConstants.BUNDLE_PRINT_QUANTITY, printQuantity);
        intent.putExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, printQuantity * pricePerPrint);
        startActivity(intent);
    }
}
