package com.o2nails.v11.ui.admin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.o2nails.v11.R;

public class SystemSettingsActivity extends Activity {

    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
