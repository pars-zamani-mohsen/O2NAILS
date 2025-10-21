package com.o2nails.v11.ui.admin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.Animator;
import android.view.animation.DecelerateInterpolator;

import com.o2nails.v11.R;
import com.o2nails.v11.utils.AppConstants;
import com.o2nails.v11.utils.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportsActivity extends Activity {

    private Button backButton;
    private Button dailyReportButton;
    private Button weeklyReportButton;
    private Button monthlyReportButton;
    private Button exportReportButton;

    private TextView totalPrintsTextView;
    private TextView totalRevenueTextView;
    private TextView averagePerDayTextView;
    private TextView reportDateTextView;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        preferenceManager = new PreferenceManager(this);
        initializeViews();
        setupClickListeners();
        setupAnimations();
        loadReportData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        dailyReportButton = findViewById(R.id.dailyReportButton);
        weeklyReportButton = findViewById(R.id.weeklyReportButton);
        monthlyReportButton = findViewById(R.id.monthlyReportButton);
        exportReportButton = findViewById(R.id.exportReportButton);

        totalPrintsTextView = findViewById(R.id.totalPrintsTextView);
        totalRevenueTextView = findViewById(R.id.totalRevenueTextView);
        averagePerDayTextView = findViewById(R.id.averagePerDayTextView);
        reportDateTextView = findViewById(R.id.reportDateTextView);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(backButton);
                finish();
            }
        });

        dailyReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(dailyReportButton);
                generateDailyReport();
            }
        });

        weeklyReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(weeklyReportButton);
                generateWeeklyReport();
            }
        });

        monthlyReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(monthlyReportButton);
                generateMonthlyReport();
            }
        });

        exportReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(exportReportButton);
                exportReport();
            }
        });
    }

    private void setupAnimations() {
        // Initial entrance animation
        dailyReportButton.setAlpha(0f);
        weeklyReportButton.setAlpha(0f);
        monthlyReportButton.setAlpha(0f);
        exportReportButton.setAlpha(0f);

        ObjectAnimator dailyFadeIn = ObjectAnimator.ofFloat(dailyReportButton, "alpha", 0f, 1f);
        ObjectAnimator weeklyFadeIn = ObjectAnimator.ofFloat(weeklyReportButton, "alpha", 0f, 1f);
        ObjectAnimator monthlyFadeIn = ObjectAnimator.ofFloat(monthlyReportButton, "alpha", 0f, 1f);
        ObjectAnimator exportFadeIn = ObjectAnimator.ofFloat(exportReportButton, "alpha", 0f, 1f);

        dailyFadeIn.setDuration(600);
        weeklyFadeIn.setDuration(600);
        monthlyFadeIn.setDuration(600);
        exportFadeIn.setDuration(600);

        weeklyFadeIn.setStartDelay(100);
        monthlyFadeIn.setStartDelay(200);
        exportFadeIn.setStartDelay(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(dailyFadeIn, weeklyFadeIn, monthlyFadeIn, exportFadeIn);
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

    private void loadReportData() {
        // Load transaction history and calculate statistics
        String transactionHistory = preferenceManager.getString(AppConstants.PREF_TRANSACTION_HISTORY, "");

        // Parse transaction history and calculate totals
        int totalPrints = 0;
        int totalRevenue = 0;

        if (!transactionHistory.isEmpty()) {
            String[] transactions = transactionHistory.split(";");
            for (String transaction : transactions) {
                if (!transaction.trim().isEmpty()) {
                    String[] parts = transaction.split(",");
                    if (parts.length >= 2) {
                        try {
                            int quantity = Integer.parseInt(parts[0]);
                            int amount = Integer.parseInt(parts[1]);
                            totalPrints += quantity;
                            totalRevenue += amount;
                        } catch (NumberFormatException e) {
                            // Handle parsing error
                        }
                    }
                }
            }
        }

        // Update UI
        totalPrintsTextView.setText(String.format(Locale.getDefault(), "%d", totalPrints));
        totalRevenueTextView.setText(String.format(Locale.getDefault(), "%d تومان", totalRevenue));

        // Calculate average per day (simplified)
        int averagePerDay = totalPrints > 0 ? totalPrints / 30 : 0; // Assuming 30 days
        averagePerDayTextView.setText(String.format(Locale.getDefault(), "%d", averagePerDay));

        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        reportDateTextView.setText(dateFormat.format(new Date()));
    }

    private void generateDailyReport() {
        // Generate daily report
        Toast.makeText(this, "گزارش روزانه تولید شد", Toast.LENGTH_SHORT).show();
        // Implementation would generate detailed daily report
    }

    private void generateWeeklyReport() {
        // Generate weekly report
        Toast.makeText(this, "گزارش هفتگی تولید شد", Toast.LENGTH_SHORT).show();
        // Implementation would generate detailed weekly report
    }

    private void generateMonthlyReport() {
        // Generate monthly report
        Toast.makeText(this, "گزارش ماهانه تولید شد", Toast.LENGTH_SHORT).show();
        // Implementation would generate detailed monthly report
    }

    private void exportReport() {
        // Export report to file
        Toast.makeText(this, "گزارش صادر شد", Toast.LENGTH_SHORT).show();
        // Implementation would export report to external storage
    }
}
