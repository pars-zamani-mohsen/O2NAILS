package com.o2nails.v11.ui.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.Nullable;

import com.o2nails.v11.R;
import com.o2nails.v11.adapters.AdminImageGridAdapter;
import com.o2nails.v11.models.ImageItem;
import com.o2nails.v11.utils.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageManagementActivity extends Activity implements AdminImageGridAdapter.OnImageDeleteListener {

    private static final int REQUEST_GALLERY = 1001;
    private static final int REQUEST_CAMERA = 1002;

    private Button backButton;
    private Button addFromGalleryButton;
    private Button addFromCameraButton;
    private GridView imageGridView;
    private TextView imageCountTextView;

    private AdminImageGridAdapter imageAdapter;
    private List<ImageItem> imageList;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_management);

        preferenceManager = new PreferenceManager(this);
        initializeViews();
        setupClickListeners();
        loadImages();
        setupGridView();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        addFromGalleryButton = findViewById(R.id.addFromGalleryButton);
        addFromCameraButton = findViewById(R.id.addFromCameraButton);
        imageGridView = findViewById(R.id.imageGridView);
        imageCountTextView = findViewById(R.id.imageCountTextView);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addFromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        // Test button for debugging
        addFromGalleryButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                testFileCreation();
                return true;
            }
        });
    }

    private void loadImages() {
        imageList = new ArrayList<>();

        // Add default images
        addDefaultImages();

        // Load custom images from preferences
        loadCustomImages();
    }

    private void addDefaultImages() {
        // Add default nail art images using direct resource IDs
        int[] defaultImageIds = {
                R.drawable.nail_art_1, R.drawable.nail_art_2, R.drawable.nail_art_3, R.drawable.nail_art_4,
                R.drawable.nail_art_5, R.drawable.nail_art_6, R.drawable.nail_art_7, R.drawable.nail_art_8
        };

        String[] defaultImageNames = {
                "nail_art_1", "nail_art_2", "nail_art_3", "nail_art_4",
                "nail_art_5", "nail_art_6", "nail_art_7", "nail_art_8"
        };

        for (int i = 0; i < defaultImageIds.length; i++) {
            ImageItem item = new ImageItem();
            item.setName(defaultImageNames[i]);
            item.setType(ImageItem.TYPE_DEFAULT);
            item.setResourceId(defaultImageIds[i]);
            imageList.add(item);
        }
    }

    private void loadCustomImages() {
        // Load custom images from preferences first
        String customImagePaths = preferenceManager.getString("custom_image_paths", "");
        Log.d("ImageManagement", "Loading custom images from preferences: " + customImagePaths);

        if (!customImagePaths.isEmpty()) {
            String[] paths = customImagePaths.split(",");
            for (String path : paths) {
                File file = new File(path);
                if (file.exists()) {
                    Log.d("ImageManagement", "Found custom image: " + file.getName());

                    ImageItem item = new ImageItem();
                    item.setName("تصویر سفارشی " + (imageList.size() - 7)); // Subtract default images count
                    item.setType(ImageItem.TYPE_GALLERY);
                    item.setFilePath(file.getAbsolutePath());
                    imageList.add(item);
                } else {
                    Log.d("ImageManagement", "Custom image file not found: " + path);
                }
            }
        }

        // Also check files directory for any additional images
        File filesDir = getFilesDir();
        File[] files = filesDir.listFiles();

        Log.d("ImageManagement", "Loading custom images from: " + filesDir.getAbsolutePath());

        if (files != null) {
            Log.d("ImageManagement", "Found " + files.length + " files in directory");

            for (File file : files) {
                if (file.getName().startsWith("custom_image_") && file.getName().endsWith(".jpg")) {
                    // Check if already loaded from preferences
                    boolean alreadyLoaded = false;
                    for (ImageItem item : imageList) {
                        if (item.getFilePath() != null && item.getFilePath().equals(file.getAbsolutePath())) {
                            alreadyLoaded = true;
                            break;
                        }
                    }

                    if (!alreadyLoaded) {
                        Log.d("ImageManagement", "Found additional custom image: " + file.getName());

                        ImageItem item = new ImageItem();
                        item.setName("تصویر سفارشی " + (imageList.size() - 7)); // Subtract default images count
                        item.setType(ImageItem.TYPE_GALLERY);
                        item.setFilePath(file.getAbsolutePath());
                        imageList.add(item);
                    }
                }
            }
        } else {
            Log.d("ImageManagement", "No files found in directory");
        }
    }

    private void setupGridView() {
        imageAdapter = new AdminImageGridAdapter(this, imageList);
        imageAdapter.setDeleteListener(this);
        imageGridView.setAdapter(imageAdapter);
        updateImageCount();
    }

    private void updateImageCount() {
        imageCountTextView.setText("تعداد تصاویر: " + imageList.size());
    }

    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_GALLERY);
        } catch (Exception e) {
            Toast.makeText(this, "خطا در باز کردن گالری: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ImageManagement", "Error opening gallery", e);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "دوربین در دسترس نیست", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    addImageFromUri(selectedImageUri);
                }
            } else if (requestCode == REQUEST_CAMERA && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    addImageFromBitmap(photo);
                }
            }
        }
    }

    private void addImageFromUri(Uri imageUri) {
        try {
            Log.d("ImageManagement", "Loading image from URI: " + imageUri.toString());

            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Toast.makeText(this, "نمی‌توان تصویر را بارگذاری کرد", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap != null) {
                Log.d("ImageManagement",
                        "Bitmap loaded successfully, size: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                addImageFromBitmap(bitmap);
            } else {
                Toast.makeText(this, "خطا در بارگذاری تصویر - فرمت پشتیبانی نمی‌شود", Toast.LENGTH_SHORT).show();
                Log.e("ImageManagement", "Failed to decode bitmap from URI");
            }
        } catch (IOException e) {
            Toast.makeText(this, "خطا در بارگذاری تصویر: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ImageManagement", "Error loading image from URI", e);
        }
    }

    private void addImageFromBitmap(Bitmap bitmap) {
        try {
            Log.d("ImageManagement", "Starting to save bitmap");

            // Create a unique filename
            String fileName = "custom_image_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);

            Log.d("ImageManagement", "File path: " + file.getAbsolutePath());

            // Ensure directory exists
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Save bitmap to file
            FileOutputStream fos = new FileOutputStream(file);
            boolean compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            Log.d("ImageManagement", "Bitmap compressed: " + compressed);
            Log.d("ImageManagement", "File exists: " + file.exists());
            Log.d("ImageManagement", "File size: " + file.length());

            if (file.exists() && file.length() > 0) {
                // Create ImageItem
                ImageItem item = new ImageItem();
                item.setName("تصویر سفارشی " + (imageList.size() - 7)); // Subtract default images count
                item.setType(ImageItem.TYPE_GALLERY);
                item.setFilePath(file.getAbsolutePath());

                // Add to list
                imageList.add(item);
                imageAdapter.notifyDataSetChanged();
                updateImageCount();

                // Save to preferences for persistence
                saveCustomImagesToPreferences();

                Toast.makeText(this, "تصویر با موفقیت اضافه شد", Toast.LENGTH_SHORT).show();
                Log.d("ImageManagement", "Image added successfully");
            } else {
                Toast.makeText(this, "خطا در ذخیره تصویر - فایل ایجاد نشد", Toast.LENGTH_SHORT).show();
                Log.e("ImageManagement", "File was not created or is empty");
            }

        } catch (IOException e) {
            Toast.makeText(this, "خطا در ذخیره تصویر: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ImageManagement", "Error saving image", e);
        }
    }

    private void saveCustomImagesToPreferences() {
        StringBuilder sb = new StringBuilder();
        for (ImageItem item : imageList) {
            if (item.getType() == ImageItem.TYPE_GALLERY) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(item.getFilePath());
            }
        }
        preferenceManager.putString("custom_image_paths", sb.toString());
        Log.d("ImageManagement", "Saved custom image paths: " + sb.toString());
    }

    @Override
    public void onImageDelete(ImageItem imageItem, int position) {
        // Don't allow deletion of default images
        if (imageItem.getType() == ImageItem.TYPE_DEFAULT) {
            Toast.makeText(this, "نمی‌توان تصاویر پیش‌فرض را حذف کرد", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("تایید حذف")
                .setMessage("آیا مطمئن هستید که می‌خواهید این تصویر را حذف کنید؟")
                .setPositiveButton("بله، حذف کن", (dialog, which) -> {
                    deleteImage(imageItem, position);
                })
                .setNegativeButton("لغو", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteImage(ImageItem imageItem, int position) {
        // Delete file if it exists
        if (imageItem.getFilePath() != null) {
            File file = new File(imageItem.getFilePath());
            if (file.exists()) {
                boolean deleted = file.delete();
                Log.d("ImageManagement", "File deleted: " + deleted);
            }
        }

        // Remove from list
        imageList.remove(position);
        imageAdapter.notifyDataSetChanged();
        updateImageCount();

        // Update preferences
        saveCustomImagesToPreferences();

        Toast.makeText(this, "تصویر حذف شد", Toast.LENGTH_SHORT).show();
    }

    private void testFileCreation() {
        try {
            Log.d("ImageManagement", "Testing file creation...");

            // Test creating a simple text file
            String fileName = "test_file_" + System.currentTimeMillis() + ".txt";
            File file = new File(getFilesDir(), fileName);

            Log.d("ImageManagement", "Test file path: " + file.getAbsolutePath());
            Log.d("ImageManagement", "Files dir exists: " + getFilesDir().exists());
            Log.d("ImageManagement", "Files dir writable: " + getFilesDir().canWrite());

            FileOutputStream fos = new FileOutputStream(file);
            fos.write("Test content".getBytes());
            fos.close();

            Log.d("ImageManagement", "Test file created: " + file.exists());
            Log.d("ImageManagement", "Test file size: " + file.length());

            Toast.makeText(this, "تست فایل: " + (file.exists() ? "موفق" : "ناموفق"), Toast.LENGTH_SHORT).show();

            // Clean up test file
            file.delete();

        } catch (Exception e) {
            Log.e("ImageManagement", "Test file creation failed", e);
            Toast.makeText(this, "تست فایل ناموفق: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the image list when returning to this activity
        loadImages();
        setupGridView();
    }
}
