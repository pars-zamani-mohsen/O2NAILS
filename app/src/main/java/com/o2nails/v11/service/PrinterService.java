package com.o2nails.v11.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.o2nails.v11.models.ImageItem;
import com.o2nails.v11.models.CartItem;

import java.util.List;

public class PrinterService {

    private static final String TAG = "PrinterService";

    public interface PrintCallback {
        void onPrintProgress(int progress, String message);

        void onPrintSuccess();

        void onPrintFailed(String errorMessage);

        void onImageChanged(ImageItem currentImage, int itemIndex, int copyIndex, int totalCopies);
    }

    private Context context;
    private PrintCallback callback;
    private Handler mainHandler;
    private boolean isPrinting = false;

    public PrinterService() {
        // Default constructor for Android Service
    }

    public PrinterService(Context context) {
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void setPrintCallback(PrintCallback callback) {
        this.callback = callback;
    }

    public void printImage(ImageItem imageItem, int quantity) {
        if (isPrinting) {
            return;
        }

        isPrinting = true;
        Log.d(TAG, "Starting print job for quantity: " + quantity);

        // Simulate printing process
        simulatePrintProcess(imageItem, quantity);
    }

    public void printMultipleImages(List<CartItem> cartItems) {
        if (isPrinting) {
            return;
        }

        isPrinting = true;
        Log.d(TAG, "Starting multi-image print job for " + cartItems.size() + " items");

        // Simulate printing process for multiple images
        simulateMultiPrintProcess(cartItems);
    }

    private void simulatePrintProcess(ImageItem imageItem, int quantity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Step 1: Initialize printer
                    notifyProgress(10, "در حال راه‌اندازی پرینتر...");
                    Thread.sleep(2000);

                    // Step 2: Load image
                    notifyProgress(25, "در حال بارگذاری تصویر...");
                    Thread.sleep(1500);

                    // Step 3: Prepare print job
                    notifyProgress(40, "در حال آماده‌سازی کار پرینت...");
                    Thread.sleep(1000);

                    // Step 4: Print each copy
                    for (int i = 0; i < quantity; i++) {
                        int progress = 40 + (i * 50 / quantity);
                        notifyProgress(progress, String.format("در حال پرینت کپی %d از %d...", i + 1, quantity));
                        Thread.sleep(3000); // Simulate print time
                    }

                    // Step 5: Finalize
                    notifyProgress(95, "در حال تکمیل پرینت...");
                    Thread.sleep(1000);

                    // Simulate success (95% success rate)
                    if (Math.random() > 0.05) {
                        notifySuccess();
                    } else {
                        notifyFailed("خطا در پرینتر");
                    }

                } catch (InterruptedException e) {
                    Log.e(TAG, "Print process interrupted", e);
                    notifyFailed("پرینت متوقف شد");
                } finally {
                    isPrinting = false;
                }
            }
        }).start();
    }

    private void simulateMultiPrintProcess(List<CartItem> cartItems) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Step 1: Initialize printer
                    notifyProgress(5, "در حال راه‌اندازی پرینتر...");
                    Thread.sleep(2000);

                    int totalItems = 0;
                    for (CartItem item : cartItems) {
                        totalItems += item.getQuantity();
                    }

                    int currentProgress = 5;
                    int itemIndex = 0;

                    // Step 2: Print each cart item
                    for (CartItem cartItem : cartItems) {
                        ImageItem imageItem = cartItem.getImageItem();
                        int quantity = cartItem.getQuantity();

                        // Load image
                        notifyProgress(currentProgress, "در حال بارگذاری تصویر " + (itemIndex + 1) + "...");
                        Thread.sleep(1000);
                        currentProgress += 5;

                        // Print each copy of this image
                        for (int i = 0; i < quantity; i++) {
                            int progress = currentProgress + (i * 80 / totalItems);
                            notifyProgress(progress, String.format("در حال پرینت %s - کپی %d از %d...",
                                    imageItem.getName(), i + 1, quantity));

                            // Notify image change for live display
                            notifyImageChanged(imageItem, itemIndex, i, quantity);

                            Thread.sleep(2000); // Simulate print time
                        }

                        currentProgress += (quantity * 80 / totalItems);
                        itemIndex++;
                    }

                    // Step 3: Finalize
                    notifyProgress(95, "در حال تکمیل پرینت...");
                    Thread.sleep(1000);

                    // Simulate success (always successful for demo)
                    notifySuccess();

                } catch (InterruptedException e) {
                    Log.e(TAG, "Multi-print process interrupted", e);
                    notifyFailed("پرینت متوقف شد");
                } finally {
                    isPrinting = false;
                }
            }
        }).start();
    }

    private void notifyProgress(int progress, String message) {
        if (callback != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onPrintProgress(progress, message);
                }
            });
        }
    }

    private void notifySuccess() {
        if (callback != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onPrintSuccess();
                }
            });
        }
    }

    private void notifyFailed(String errorMessage) {
        if (callback != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onPrintFailed(errorMessage);
                }
            });
        }
    }

    private void notifyImageChanged(ImageItem currentImage, int itemIndex, int copyIndex, int totalCopies) {
        if (callback != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onImageChanged(currentImage, itemIndex, copyIndex, totalCopies);
                }
            });
        }
    }

    public boolean isPrinting() {
        return isPrinting;
    }

    public void cancelPrint() {
        if (isPrinting) {
            isPrinting = false;
            notifyFailed("پرینت لغو شد");
        }
    }

    public void cleanup() {
        isPrinting = false;
        callback = null;
    }

    // Mock printer integration methods
    public boolean isPrinterConnected() {
        // In real implementation, this would check actual printer connection
        return true;
    }

    public String getPrinterStatus() {
        // In real implementation, this would return actual printer status
        return "READY";
    }

    public void testPrinter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    notifyProgress(100, "تست پرینتر موفقیت‌آمیز");
                } catch (InterruptedException e) {
                    notifyFailed("خطا در تست پرینتر");
                }
            }
        }).start();
    }
}
