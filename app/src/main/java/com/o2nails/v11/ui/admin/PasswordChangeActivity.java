package com.o2nails.v11.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.o2nails.v11.R;
import com.o2nails.v11.utils.AppConstants;
import com.o2nails.v11.utils.PreferenceManager;

public class PasswordChangeActivity extends AppCompatActivity {

    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private TextView statusTextView;
    private Button changePasswordButton;
    private Button cancelButton;
    private Button backButton;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        preferenceManager = new PreferenceManager(this);
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        statusTextView = findViewById(R.id.statusTextView);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        cancelButton = findViewById(R.id.cancelButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(currentPassword)) {
            showStatus("لطفاً رمز عبور فعلی را وارد کنید", false);
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            showStatus("لطفاً رمز عبور جدید را وارد کنید", false);
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            showStatus("لطفاً تکرار رمز عبور جدید را وارد کنید", false);
            return;
        }

        if (currentPassword.length() != 8) {
            showStatus("رمز عبور فعلی باید 8 رقمی باشد", false);
            return;
        }

        if (newPassword.length() != 8) {
            showStatus("رمز عبور جدید باید 8 رقمی باشد", false);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showStatus("رمز عبور جدید و تکرار آن مطابقت ندارند", false);
            return;
        }

        // Check if current password is correct
        if (!currentPassword.equals(AppConstants.ADMIN_PASSWORD)) {
            showStatus("رمز عبور فعلی اشتباه است", false);
            return;
        }

        // Check if new password is different from current
        if (currentPassword.equals(newPassword)) {
            showStatus("رمز عبور جدید باید با رمز عبور فعلی متفاوت باشد", false);
            return;
        }

        // Save new password to preferences
        preferenceManager.putString("admin_password", newPassword);
        
        showStatus("رمز عبور با موفقیت تغییر کرد", true);
        
        // Clear input fields
        currentPasswordEditText.setText("");
        newPasswordEditText.setText("");
        confirmPasswordEditText.setText("");

        // Show success message
        Toast.makeText(this, "رمز عبور با موفقیت تغییر کرد", Toast.LENGTH_LONG).show();

        // Finish activity after a short delay
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    private void showStatus(String message, boolean isSuccess) {
        statusTextView.setText(message);
        
        if (isSuccess) {
            statusTextView.setTextColor(getResources().getColor(R.color.md_theme_light_primary));
        } else {
            statusTextView.setTextColor(getResources().getColor(R.color.md_theme_light_error));
        }
    }
}
