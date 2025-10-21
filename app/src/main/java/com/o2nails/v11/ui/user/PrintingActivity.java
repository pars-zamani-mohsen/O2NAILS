package com.o2nails.v11.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.o2nails.v11.R;
import com.o2nails.v11.utils.AppConstants;
import com.o2nails.v11.models.ImageItem;
import com.o2nails.v11.models.CartItem;
import com.o2nails.v11.service.PrinterService;
import com.o2nails.v11.adapters.PrintQueueAdapter;

import java.util.List;

public class PrintingActivity extends Activity implements PrinterService.PrintCallback {

    private ImageView previewImageView;
    private TextView statusTextView;
    private TextView currentImageNameTextView;
    private ProgressBar progressBar;
    private Button backButton;
    private ListView printQueueListView;

    private ImageItem selectedImage;
    private int printQuantity;
    private int totalAmount;
    private String transactionId;
    private List<CartItem> cartItems;
    private int totalItems;
    private PrinterService printerService;
    private Handler handler;
    private PrintQueueAdapter printQueueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printing);

        printerService = new PrinterService(this);
        printerService.setPrintCallback(this);
        handler = new Handler();

        initializeViews();
        loadData();
        setupPrintQueue();
        startPrinting();
    }

    private void initializeViews() {
        previewImageView = findViewById(R.id.previewImageView);
        statusTextView = findViewById(R.id.statusTextView);
        currentImageNameTextView = findViewById(R.id.currentImageNameTextView);
        progressBar = findViewById(R.id.progressBar);
        backButton = findViewById(R.id.backButton);
        printQueueListView = findViewById(R.id.printQueueListView);
    }

    private void loadData() {
        // Try to get cart items first (new multi-item flow)
        cartItems = (List<CartItem>) getIntent().getSerializableExtra(AppConstants.BUNDLE_CART_ITEMS);

        if (cartItems != null && !cartItems.isEmpty()) {
            // Multi-item flow
            totalAmount = getIntent().getIntExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, 0);
            totalItems = getIntent().getIntExtra(AppConstants.BUNDLE_TOTAL_ITEMS, 0);
            transactionId = getIntent().getStringExtra(AppConstants.BUNDLE_TRANSACTION_ID);

            // Use first image as preview
            selectedImage = cartItems.get(0).getImageItem();
            printQuantity = totalItems; // Total quantity for all items
        } else {
            // Fallback to single item flow
            selectedImage = (ImageItem) getIntent().getSerializableExtra(AppConstants.BUNDLE_SELECTED_IMAGE);
            printQuantity = getIntent().getIntExtra(AppConstants.BUNDLE_PRINT_QUANTITY, 1);
            totalAmount = getIntent().getIntExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, 0);
            transactionId = getIntent().getStringExtra(AppConstants.BUNDLE_TRANSACTION_ID);
        }

        if (selectedImage == null) {
            Toast.makeText(this, "خطا در بارگذاری اطلاعات", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayImage();
    }

    private void setupPrintQueue() {
        if (cartItems != null && !cartItems.isEmpty()) {
            printQueueAdapter = new PrintQueueAdapter(this, cartItems);
            printQueueListView.setAdapter(printQueueAdapter);
        }
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
        if (cartItems != null && !cartItems.isEmpty()) {
            // Multi-item printing
            printerService.printMultipleImages(cartItems);
        } else {
            // Single item printing
            printerService.printImage(selectedImage, printQuantity);
        }
    }

    @Override
    public void onPrintProgress(int progress, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
                statusTextView.setText(message);

                // Update current image display and queue status
                updateCurrentImageDisplay(message);
            }
        });
    }

    private void updateCurrentImageDisplay(String message) {
        // This method is now handled by onImageChanged callback
    }

    @Override
    public void onImageChanged(ImageItem currentImage, int itemIndex, int copyIndex, int totalCopies) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update current image display
                if (currentImage.getType() == ImageItem.TYPE_DEFAULT) {
                    previewImageView.setImageResource(currentImage.getResourceId());
                } else {
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory
                            .decodeFile(currentImage.getFilePath());
                    if (bitmap != null) {
                        previewImageView.setImageBitmap(bitmap);
                    }
                }
                currentImageNameTextView
                        .setText(currentImage.getName() + " (کپی " + (copyIndex + 1) + " از " + totalCopies + ")");

                // Update queue status
                if (printQueueAdapter != null) {
                    printQueueAdapter.setCurrentPrintingIndex(itemIndex);
                }
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

        if (cartItems != null && !cartItems.isEmpty()) {
            // Multi-item flow
            intent.putExtra(AppConstants.BUNDLE_CART_ITEMS, new java.util.ArrayList<>(cartItems));
            intent.putExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, totalAmount);
            intent.putExtra(AppConstants.BUNDLE_TOTAL_ITEMS, totalItems);
        } else {
            // Single item flow (fallback)
            intent.putExtra(AppConstants.BUNDLE_SELECTED_IMAGE, selectedImage);
            intent.putExtra(AppConstants.BUNDLE_PRINT_QUANTITY, printQuantity);
            intent.putExtra(AppConstants.BUNDLE_TOTAL_AMOUNT, totalAmount);
        }

        intent.putExtra(AppConstants.BUNDLE_TRANSACTION_ID, transactionId);
        startActivity(intent);
        finish();
    }
}
