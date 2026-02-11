package com.example.foodorderingapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.SwitchCompat;

import com.example.foodorderingapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initView();
    }

    private void initView() {
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());

        // Theme Switch
        SwitchCompat themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setChecked(themeManager.isDarkMode());
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            themeManager.setTheme(isChecked);
        });

        // Language Group
        RadioGroup languageGroup = findViewById(R.id.languageGroup);
        RadioButton englishRb = findViewById(R.id.englishRb);
        RadioButton arabicRb = findViewById(R.id.arabicRb);

        if (languageManager.getLanguage().equals("ar")) {
            arabicRb.setChecked(true);
        } else {
            englishRb.setChecked(true);
        }

        languageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String lang = (checkedId == R.id.arabicRb) ? "ar" : "en";
            if (!lang.equals(languageManager.getLanguage())) {
                languageManager.setLocale(lang);
                // Restart activity to apply language
                recreate();
            }
        });

        findViewById(R.id.logoutBtn).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SettingsActivity.this, IntroActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
