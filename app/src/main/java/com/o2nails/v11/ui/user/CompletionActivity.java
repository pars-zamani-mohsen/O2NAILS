package com.o2nails.v11.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.o2nails.v11.R;
import com.o2nails.v11.utils.AppConstants;
import com.o2nails.v11.utils.PreferenceManager;
import com.o2nails.v11.models.ImageItem;

public class CompletionActivity extends Activity {

    private ImageView previewImageView;
    private TextView thankYouTextView;
    private TextView completionTextView;
    private Button newPrintButton;
    private Button finishButton;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion);

        preferenceManager = new PreferenceManager(this);
        initializeViews();
        loadData();
        setupClickListeners();
        saveTransactionHistory();
    }

    private void initializeViews() {
        previewImageView = findViewById(R.id.previewImageView);
        thankYouTextView = findViewById(R.id.thankYouTextView);
        completionTextView = findViewById(R.id.completionTextView);
        newPrintButton = findViewById(R.id.newPrintButton);
        finishButton = findViewById(R.id.finishButton);
    }

    private void loadData() {
        ImageItem selectedImage = (ImageItem) getIntent().getSerializableExtra(AppConstants.BUNDLE_SELECTED_IMAGE);
        int printQuantity = getIntent().getIntExtra(AppConstants.BUNDLE_PRINT_QUANTITY, 1);
        int totalAmount = getIntent().getIntExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, 0);
        String transactionId = getIntent().getStringExtra(AppConstants.BUNDLE_TRANSACTION_ID);

        if (selectedImage != null) {
            displayImage(selectedImage);
        }

        updateCompletionMessage(printQuantity, totalAmount, transactionId);
    }

    private void displayImage(ImageItem selectedImage) {
        if (selectedImage.getType() == ImageItem.TYPE_DEFAULT) {
            previewImageView.setImageResource(selectedImage.getResourceId());
        } else {
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(selectedImage.getFilePath());
            if (bitmap != null) {
                previewImageView.setImageBitmap(bitmap);
            }
        }
    }

    private void updateCompletionMessage(int printQuantity, int totalAmount, String transactionId) {
        String message = String.format("پرینت %d عدد با موفقیت انجام شد\nمبلغ: %s تومان\nشماره تراکنش: %s",
                printQuantity, formatPrice(totalAmount), transactionId);
        completionTextView.setText(message);
    }

    private String formatPrice(int price) {
        return String.format("%,d", price);
    }

    private void setupClickListeners() {
        newPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to main screen
                Intent intent = new Intent(CompletionActivity.this, com.o2nails.v11.MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveTransactionHistory() {
        ImageItem selectedImage = (ImageItem) getIntent().getSerializableExtra(AppConstants.BUNDLE_SELECTED_IMAGE);
        int printQuantity = getIntent().getIntExtra(AppConstants.BUNDLE_PRINT_QUANTITY, 1);
        int totalAmount = getIntent().getIntExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, 0);
        String transactionId = getIntent().getStringExtra(AppConstants.BUNDLE_TRANSACTION_ID);

        if (selectedImage != null && transactionId != null) {
            // Get existing transaction history
            String existingHistory = preferenceManager.getString(AppConstants.PREF_TRANSACTION_HISTORY, "");

            // Create new transaction record
            String newTransaction = printQuantity + "," + totalAmount + "," +
                    selectedImage.getName() + "," +
                    System.currentTimeMillis() + "," +
                    transactionId;

            // Append to existing history
            String updatedHistory = existingHistory.isEmpty() ? newTransaction : existingHistory + ";" + newTransaction;

            // Save updated history
            preferenceManager.putString(AppConstants.PREF_TRANSACTION_HISTORY, updatedHistory);
        }
    }
}
