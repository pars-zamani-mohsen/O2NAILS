# 🐳 راهنمای تست APK با Docker

## 📋 پیش‌نیازها
- Docker و Docker Compose نصب شده
- حداقل 4GB RAM آزاد
- پورت‌های 6080, 8080, 5037, 5554, 5555 آزاد

## 🚀 راه‌اندازی محیط تست

### 1. شروع کانتینرها
```bash
# شروع تمام سرویس‌ها
docker-compose up -d

# مشاهده لاگ‌ها
docker-compose logs -f android-emulator
```

### 2. دسترسی به شبیه‌ساز
- **VNC Web Interface:** http://localhost:6080
- **Android Studio:** http://localhost:8080
- **ADB Server:** localhost:5037

## 📱 تست APK

### روش 1: استفاده از اسکریپت خودکار
```bash
./test_apk.sh
```

### روش 2: تست دستی
```bash
# اتصال به ADB
docker exec -it adb-server adb connect android-emulator:5555

# بررسی دستگاه‌ها
docker exec -it adb-server adb devices

# نصب APK
docker exec -it adb-server adb install /root/tmp/O2Nails_Simple.apk

# اجرای اپلیکیشن
docker exec -it adb-server adb shell am start -n com.o2nails.v11/.MainActivity
```

## 🔧 دستورات مفید

### مدیریت کانتینرها
```bash
# توقف سرویس‌ها
docker-compose down

# راه‌اندازی مجدد
docker-compose restart

# مشاهده وضعیت
docker-compose ps
```

### دستورات ADB
```bash
# اتصال به شبیه‌ساز
docker exec -it adb-server adb connect android-emulator:5555

# لیست اپلیکیشن‌ها
docker exec -it adb-server adb shell pm list packages

# حذف اپلیکیشن
docker exec -it adb-server adb uninstall com.o2nails.v11

# لاگ‌های سیستم
docker exec -it adb-server adb logcat
```

### دسترسی به فایل‌ها
```bash
# کپی APK جدید
cp your_new_apk.apk apk_files/

# دسترسی به لاگ‌ها
docker exec -it android-emulator cat /var/log/android.log
```

## 🌐 دسترسی از مرورگر

### VNC Web Interface
1. به آدرس http://localhost:6080 بروید
2. روی "Connect" کلیک کنید
3. شبیه‌ساز اندروید را مشاهده کنید

### Android Studio
1. به آدرس http://localhost:8080 بروید
2. Android Studio را در مرورگر اجرا کنید

## 🐛 عیب‌یابی

### مشکلات رایج
```bash
# بررسی وضعیت کانتینرها
docker-compose ps

# مشاهده لاگ‌های خطا
docker-compose logs android-emulator

# راه‌اندازی مجدد
docker-compose restart android-emulator
```

### پاک کردن داده‌ها
```bash
# توقف و حذف کانتینرها
docker-compose down -v

# حذف تصاویر
docker rmi budtmo/docker-android:emulator_11.0
```

## 📊 مشخصات سیستم

### حداقل نیازمندی‌ها
- **RAM:** 4GB
- **CPU:** 2 Core
- **Disk:** 10GB فضای آزاد
- **OS:** Linux/Windows/macOS

### پورت‌های استفاده شده
- **6080:** VNC Web Interface
- **8080:** Android Studio
- **5037:** ADB Server
- **5554-5555:** ADB Connection

## 🎯 مزایای این روش

1. **ایزوله:** محیط کاملاً جدا از سیستم اصلی
2. **قابل تکرار:** نتایج یکسان در هر بار اجرا
3. **سریع:** راه‌اندازی در کمتر از 5 دقیقه
4. **کامل:** شامل تمام ابزارهای مورد نیاز
5. **قابل تنظیم:** امکان تغییر تنظیمات شبیه‌ساز

## 🔄 به‌روزرسانی APK

```bash
# کپی APK جدید
cp O2Nails_Simple.apk apk_files/

# حذف نسخه قبلی
docker exec -it adb-server adb uninstall com.o2nails.v11

# نصب نسخه جدید
docker exec -it adb-server adb install /root/tmp/O2Nails_Simple.apk
```

