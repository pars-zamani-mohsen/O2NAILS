package com.o2nails.v11.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.Animator;
import android.view.animation.DecelerateInterpolator;

import java.io.File;

import com.o2nails.v11.R;
import com.o2nails.v11.utils.AppConstants;
import com.o2nails.v11.utils.PreferenceManager;
import com.o2nails.v11.utils.CartManager;
import com.o2nails.v11.adapters.ImageGridAdapter;
import com.o2nails.v11.models.ImageItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageSelectionActivity extends Activity {

    private GridView imageGridView;
    private ImageView previewImageView;
    private Button galleryButton;
    private Button cameraButton;
    private Button nextButton;
    private Button backButton;

    private ImageGridAdapter imageAdapter;
    private List<ImageItem> imageList;
    private ImageItem selectedImage;
    private PreferenceManager preferenceManager;
    private CartManager cartManager;

    private static final int REQUEST_GALLERY = 1001;
    private static final int REQUEST_CAMERA = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);

        preferenceManager = new PreferenceManager(this);
        cartManager = CartManager.getInstance();
        cartManager.setContext(this);
        initializeViews();
        setupImageList();
        setupClickListeners();
        setupAnimations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload custom images in case new ones were added
        reloadCustomImages();
        // Refresh adapter when returning to this activity
        if (imageAdapter != null) {
            imageAdapter.notifyDataSetChanged();
        }
        // Update next button state
        if (nextButton != null) {
            nextButton.setEnabled(!cartManager.isEmpty());
        }
    }

    private void reloadCustomImages() {
        // Remove existing custom images from list
        imageList.removeIf(item -> item.getType() == ImageItem.TYPE_GALLERY);

        // Reload custom images
        loadCustomImages();
    }

    private void initializeViews() {
        imageGridView = findViewById(R.id.imageGridView);
        previewImageView = findViewById(R.id.previewImageView);
        galleryButton = findViewById(R.id.galleryButton);
        cameraButton = findViewById(R.id.cameraButton);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupImageList() {
        imageList = new ArrayList<>();

        // Add default nail art images from assets
        addDefaultImages();

        // Add favorite images from preferences
        addFavoriteImages();

        imageAdapter = new ImageGridAdapter(this, imageList, new ImageGridAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(ImageItem imageItem) {
                // Add to cart with default quantity of 1
                cartManager.addToCart(imageItem, 1);

                // Update preview to show the last selected image
                selectedImage = imageItem;
                showImagePreview(imageItem);

                // Enable next button if cart is not empty
                nextButton.setEnabled(!cartManager.isEmpty());

                // Refresh the adapter to show selection state
                imageAdapter.notifyDataSetChanged();

                // Show feedback
                Toast.makeText(ImageSelectionActivity.this,
                        "تصویر به سبد خرید اضافه شد", Toast.LENGTH_SHORT).show();
            }
        });

        imageGridView.setAdapter(imageAdapter);
    }

    private void addDefaultImages() {
        // Add default nail art images using direct resource IDs
        int[] defaultImageIds = {
                R.drawable.test_image, R.drawable.nail_art_1, R.drawable.nail_art_2, R.drawable.nail_art_3,
                R.drawable.nail_art_4, R.drawable.nail_art_5, R.drawable.nail_art_6, R.drawable.nail_art_7
        };

        String[] defaultImageNames = {
                "test_image", "nail_art_1", "nail_art_2", "nail_art_3",
                "nail_art_4", "nail_art_5", "nail_art_6", "nail_art_7"
        };

        for (int i = 0; i < defaultImageIds.length; i++) {
            ImageItem item = new ImageItem();
            item.setName(defaultImageNames[i]);
            item.setType(ImageItem.TYPE_DEFAULT);
            item.setResourceId(defaultImageIds[i]);
            imageList.add(item);
        }

        // Load custom images from admin management
        loadCustomImages();
    }

    private void loadCustomImages() {
        // Load custom images from preferences first
        String customImagePaths = preferenceManager.getString("custom_image_paths", "");

        if (!customImagePaths.isEmpty()) {
            String[] paths = customImagePaths.split(",");
            for (String path : paths) {
                File file = new File(path);
                if (file.exists()) {
                    ImageItem item = new ImageItem();
                    item.setName("تصویر سفارشی");
                    item.setType(ImageItem.TYPE_GALLERY);
                    item.setFilePath(file.getAbsolutePath());
                    imageList.add(item);
                }
            }
        }

        // Also check files directory for any additional images
        File filesDir = getFilesDir();
        File[] files = filesDir.listFiles();

        if (files != null) {
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
                        ImageItem item = new ImageItem();
                        item.setName("تصویر سفارشی");
                        item.setType(ImageItem.TYPE_GALLERY);
                        item.setFilePath(file.getAbsolutePath());
                        imageList.add(item);
                    }
                }
            }
        }
    }

    private void addFavoriteImages() {
        // Load favorite images from preferences
        String favoriteImagesJson = preferenceManager.getString(AppConstants.PREF_FAVORITE_IMAGES, "");
        if (!favoriteImagesJson.isEmpty()) {
            try {
                // Simple implementation for favorite images
                String[] favoriteImageNames = favoriteImagesJson.split(",");
                for (String imageName : favoriteImageNames) {
                    if (!imageName.trim().isEmpty()) {
                        ImageItem item = new ImageItem();
                        item.setName("محبوب: " + imageName);
                        item.setType(ImageItem.TYPE_FAVORITE);
                        item.setResourceId(
                                getResources().getIdentifier(imageName.trim(), "drawable", getPackageName()));
                        if (item.getResourceId() != 0) {
                            imageList.add(item);
                        }
                    }
                }
            } catch (Exception e) {
                // Handle parsing error
            }
        }
    }

    private void setupClickListeners() {
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(galleryButton);
                openGallery();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(cameraButton);
                openCamera();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cartManager.isEmpty()) {
                    animateButtonClick(nextButton);
                    proceedToCartManagement();
                } else {
                    Toast.makeText(ImageSelectionActivity.this,
                            "لطفاً حداقل یک تصویر انتخاب کنید", Toast.LENGTH_SHORT).show();
                }
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

    private void setupAnimations() {
        // Initial entrance animation
        imageGridView.setAlpha(0f);
        previewImageView.setAlpha(0f);

        ObjectAnimator gridFadeIn = ObjectAnimator.ofFloat(imageGridView, "alpha", 0f, 1f);
        ObjectAnimator previewFadeIn = ObjectAnimator.ofFloat(previewImageView, "alpha", 0f, 1f);

        gridFadeIn.setDuration(600);
        previewFadeIn.setDuration(600);
        previewFadeIn.setStartDelay(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(gridFadeIn, previewFadeIn);
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

    private void showImagePreview(ImageItem imageItem) {
        if (imageItem.getType() == ImageItem.TYPE_DEFAULT) {
            previewImageView.setImageResource(imageItem.getResourceId());
        } else {
            // Load from file path
            Bitmap bitmap = BitmapFactory.decodeFile(imageItem.getFilePath());
            if (bitmap != null) {
                previewImageView.setImageBitmap(bitmap);
            }
        }

        // Animate preview appearance
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(previewImageView, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(previewImageView, "scaleY", 0.8f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "دوربین در دسترس نیست", Toast.LENGTH_SHORT).show();
        }
    }

    private void proceedToCartManagement() {
        // For now, we'll create a new CartManagementActivity
        // But first, let's create a simple cart summary and go to quantity selection
        Intent intent = new Intent(this, CartManagementActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY && data != null) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    processSelectedImage(imageUri, ImageItem.TYPE_GALLERY);
                }
            } else if (requestCode == REQUEST_CAMERA && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    processCameraImage(bitmap);
                }
            }
        }
    }

    private void processSelectedImage(Uri imageUri, int type) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap != null) {
                // Save image to internal storage
                String fileName = "selected_image_" + System.currentTimeMillis() + ".jpg";
                File file = new File(getFilesDir(), fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, AppConstants.IMAGE_QUALITY, fos);
                fos.close();

                // Create image item
                ImageItem imageItem = new ImageItem();
                imageItem.setName("تصویر انتخاب شده");
                imageItem.setType(type);
                imageItem.setFilePath(file.getAbsolutePath());

                // Add to cart
                cartManager.addToCart(imageItem, 1);

                selectedImage = imageItem;
                showImagePreview(imageItem);
                nextButton.setEnabled(!cartManager.isEmpty());

                // Refresh adapter
                imageAdapter.notifyDataSetChanged();

                Toast.makeText(this, "تصویر به سبد خرید اضافه شد", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.error_image_load), Toast.LENGTH_SHORT).show();
        }
    }

    private void processCameraImage(Bitmap bitmap) {
        try {
            // Save image to internal storage
            String fileName = "camera_image_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, AppConstants.IMAGE_QUALITY, fos);
            fos.close();

            // Create image item
            ImageItem imageItem = new ImageItem();
            imageItem.setName("تصویر دوربین");
            imageItem.setType(ImageItem.TYPE_CAMERA);
            imageItem.setFilePath(file.getAbsolutePath());

            // Add to cart
            cartManager.addToCart(imageItem, 1);

            selectedImage = imageItem;
            showImagePreview(imageItem);
            nextButton.setEnabled(!cartManager.isEmpty());

            // Refresh adapter
            imageAdapter.notifyDataSetChanged();

            Toast.makeText(this, "تصویر به سبد خرید اضافه شد", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.error_image_load), Toast.LENGTH_SHORT).show();
        }
    }
}
