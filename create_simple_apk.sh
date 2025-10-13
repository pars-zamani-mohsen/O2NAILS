#!/bin/bash

echo "🔧 ایجاد APK ساده و قابل نصب..."

# حذف فایل‌های قبلی
rm -f O2Nails_Simple_Final.apk

# ایجاد APK با ساختار صحیح و بدون فشرده‌سازی resources.arsc
echo "📦 ایجاد APK با ساختار صحیح..."
cd /var/www/html/O2NAILS

# ایجاد APK با zip و تنظیمات صحیح (بدون فشرده‌سازی resources.arsc)
zip -r -0 O2Nails_Simple_Final.apk \
    AndroidManifest.xml \
    classes.dex \
    classes2.dex \
    resources.arsc

# اضافه کردن سایر فایل‌ها با فشرده‌سازی
zip -r O2Nails_Simple_Final.apk \
    res/ \
    assets/ \
    lib/ \
    kotlin/ \
    com/ \
    DebugProbesKt.bin \
    META-INF/

# تراز کردن APK
echo "⚖️ تراز کردن APK..."
zipalign -v 4 O2Nails_Simple_Final.apk O2Nails_Simple_Final_Aligned.apk

# امضا کردن APK با apksigner
echo "✍️ امضا کردن APK با apksigner..."
apksigner sign \
    --ks test.keystore \
    --ks-key-alias test \
    --ks-pass pass:test123 \
    --key-pass pass:test123 \
    --out O2Nails_Simple_Final_Signed.apk \
    O2Nails_Simple_Final_Aligned.apk

# بررسی امضا
echo "🔍 بررسی امضا..."
apksigner verify --verbose O2Nails_Simple_Final_Signed.apk

echo "✅ APK نهایی آماده شد: O2Nails_Simple_Final_Signed.apk"
