# 🎯 O2Nails Android Application

## 📱 فایل‌های اصلی

### APK نهایی
- **`O2Nails_Simple_Final_Signed.apk`** - APK نهایی و قابل نصب (47.8 MB)
  - امضا شده با `test.keystore`
  - سازگار با Android 13 (API 33)
  - آماده برای نصب و استفاده

### کلیدهای امضا
- **`test.keystore`** - کلید امضا برای APK (رمز: test123)
- **`o2nails-key.keystore`** - کلید اصلی O2Nails

### محیط تست Docker
- **`docker-compose.yml`** - تنظیمات شبیه‌ساز اندروید
- **`README_Docker_Testing.md`** - راهنمای استفاده از Docker

### اسکریپت‌های مفید
- **`create_simple_apk.sh`** - اسکریپت ایجاد APK جدید

## 🚀 نحوه استفاده

### نصب APK
```bash
adb install O2Nails_Simple_Final_Signed.apk
```

### اجرای اپلیکیشن
```bash
adb shell am start -n com.o2nails.v11/.MainActivity
```

### راه‌اندازی محیط تست
```bash
docker-compose up -d
# دسترسی به شبیه‌ساز: http://localhost:6080
```

## 📋 مشخصات اپلیکیشن
- **Package:** com.o2nails.v11
- **Version:** 2.8.4.8 (336)
- **Min SDK:** 23 (Android 6.0)
- **Target SDK:** 33 (Android 13)
- **Architecture:** ARM64, ARMv7

## ✅ وضعیت
- ✅ APK کاملاً آماده و قابل نصب
- ✅ تست شده در شبیه‌ساز اندروید
- ✅ سازگار با Android 13
- ✅ تمام قابلیت‌ها فعال
