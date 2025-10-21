package com.o2nails.v11.utils;

public class AppConstants {

    // Admin Settings
    public static final String ADMIN_PASSWORD = "12345678"; // 8-digit password
    public static final int MAX_ADMIN_FAILED_ATTEMPTS = 3;
    public static final long ADMIN_LOCK_DURATION = 10 * 60 * 1000; // 10 minutes in milliseconds

    // Preferences Keys
    public static final String PREF_ADMIN_FAILED_ATTEMPTS = "admin_failed_attempts";
    public static final String PREF_ADMIN_LOCK_TIME = "admin_lock_time";
    public static final String PREF_PRINT_PRICE = "print_price";
    public static final String PREF_PRINTER_SETTINGS = "printer_settings";
    public static final String PREF_FAVORITE_IMAGES = "favorite_images";
    public static final String PREF_TRANSACTION_HISTORY = "transaction_history";
    public static final String PREF_SYSTEM_SETTINGS = "system_settings";

    // Default Values
    public static final int DEFAULT_PRINT_PRICE = 5000; // 5000 Toman
    public static final int DEFAULT_PRINT_QUANTITY = 1;

    // Request Codes
    public static final int REQUEST_IMAGE_PICKER = 1001;
    public static final int REQUEST_CAMERA = 1002;
    public static final int REQUEST_PAYMENT = 1003;
    public static final int REQUEST_ADMIN_LOGIN = 1004;

    // Bundle Keys
    public static final String BUNDLE_SELECTED_IMAGE = "selected_image";
    public static final String BUNDLE_PRINT_QUANTITY = "print_quantity";
    public static final String BUNDLE_TOTAL_AMOUNT = "total_amount";
    public static final String BUNDLE_TOTAL_ITEMS = "total_items";
    public static final String BUNDLE_CART_ITEMS = "cart_items";
    public static final String BUNDLE_PAYMENT_SUCCESS = "payment_success";
    public static final String BUNDLE_TRANSACTION_ID = "transaction_id";

    // Printer Settings
    public static final String PRINTER_TYPE_T1200 = "T1200";
    public static final String PRINTER_TYPE_T1300 = "T1300";
    public static final String COATING_TYPE_PP = "PP";
    public static final String COATING_TYPE_PG = "PG";

    // Image Settings
    public static final int MAX_IMAGE_SIZE = 2048; // pixels
    public static final int IMAGE_QUALITY = 90;
    public static final String IMAGE_FORMAT = "JPEG";

    // Animation Durations
    public static final int ANIMATION_DURATION_SHORT = 200;
    public static final int ANIMATION_DURATION_MEDIUM = 400;
    public static final int ANIMATION_DURATION_LONG = 600;

    // UI Constants
    public static final int BUTTON_MIN_HEIGHT = 120; // dp
    public static final int BUTTON_MIN_WIDTH = 200; // dp
    public static final int LARGE_TEXT_SIZE = 24; // sp
    public static final int MEDIUM_TEXT_SIZE = 18; // sp
    public static final int SMALL_TEXT_SIZE = 14; // sp

    // Error Messages
    public static final String ERROR_NETWORK = "خطا در اتصال به شبکه";
    public static final String ERROR_PRINTER = "خطا در اتصال به پرینتر";
    public static final String ERROR_PAYMENT = "خطا در پردازش پرداخت";
    public static final String ERROR_IMAGE_LOAD = "خطا در بارگذاری تصویر";
    public static final String ERROR_INVALID_PASSWORD = "رمز عبور اشتباه است";
    public static final String ERROR_ADMIN_LOCKED = "دسترسی مدیر قفل شده است";

    // Success Messages
    public static final String SUCCESS_PAYMENT = "پرداخت با موفقیت انجام شد";
    public static final String SUCCESS_PRINT = "پرینت با موفقیت انجام شد";
    public static final String SUCCESS_ADMIN_LOGIN = "ورود موفقیت‌آمیز";
    public static final String SUCCESS_SETTINGS_SAVED = "تنظیمات ذخیره شد";
}
