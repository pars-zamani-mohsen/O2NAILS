package com.o2nails.v11.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.o2nails.v11.R;
import com.o2nails.v11.utils.AppConstants;
import com.o2nails.v11.models.ImageItem;
import com.o2nails.v11.service.PrinterService;

public class PrintingActivity extends Activity implements PrinterService.PrintCallback {

    private ImageView previewImageView;
    private TextView statusTextView;
    private ProgressBar progressBar;
    private Button backButton;

    private ImageItem selectedImage;
    private int printQuantity;
    private int totalAmount;
    private String transactionId;
    private PrinterService printerService;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printing);

        printerService = new PrinterService(this);
        printerService.setPrintCallback(this);
        handler = new Handler();

        initializeViews();
        loadData();
        startPrinting();
    }

    private void initializeViews() {
        previewImageView = findViewById(R.id.previewImageView);
        statusTextView = findViewById(R.id.statusTextView);
        progressBar = findViewById(R.id.progressBar);
        backButton = findViewById(R.id.backButton);
    }

    private void loadData() {
        selectedImage = (ImageItem) getIntent().getSerializableExtra(AppConstants.BUNDLE_SELECTED_IMAGE);
        printQuantity = getIntent().getIntExtra(AppConstants.BUNDLE_PRINT_QUANTITY, 1);
        totalAmount = getIntent().getIntExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, 0);
        transactionId = getIntent().getStringExtra(AppConstants.BUNDLE_TRANSACTION_ID);

        if (selectedImage == null) {
            Toast.makeText(this, "خطا در بارگذاری اطلاعات", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayImage();
    }

    private void displayImage() {
        if (selectedImage.getType() == ImageItem.TYPE_DEFAULT) {
            previewImageView.setImageResource(selectedImage.getResourceId());
        } else {
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(selectedImage.getFilePath());
            if (bitmap != null) {
                previewImageView.setImageBitmap(bitmap);
            }
        }
    }

    private void startPrinting() {
        statusTextView.setText("در حال آماده‌سازی پرینت...");
        progressBar.setProgress(0);

        // Start printing process
        printerService.printImage(selectedImage, printQuantity);
    }

    @Override
    public void onPrintProgress(int progress, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
                statusTextView.setText(message);
            }
        });
    }

    @Override
    public void onPrintSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(getString(R.string.success_print));
                progressBar.setProgress(100);
                Toast.makeText(PrintingActivity.this, getString(R.string.success_print), Toast.LENGTH_SHORT).show();

                // Proceed to completion screen
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proceedToCompletion();
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onPrintFailed(String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText("خطا در پرینت: " + errorMessage);
                Toast.makeText(PrintingActivity.this, "خطا در پرینت", Toast.LENGTH_LONG).show();

                backButton.setVisibility(View.VISIBLE);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        });
    }

    private void proceedToCompletion() {
        Intent intent = new Intent(this, CompletionActivity.class);
        intent.putExtra(AppConstants.BUNDLE_SELECTED_IMAGE, selectedImage);
        intent.putExtra(AppConstants.BUNDLE_PRINT_QUANTITY, printQuantity);
        intent.putExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, totalAmount);
        intent.putExtra(AppConstants.BUNDLE_TRANSACTION_ID, transactionId);
        startActivity(intent);
        finish();
    }
}
