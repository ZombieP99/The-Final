package com.example.foodorderingapp.Activity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.Helper.LanguageManager;
import com.example.foodorderingapp.Helper.ThemeManager;

public class BaseActivity extends AppCompatActivity {
    protected LanguageManager languageManager;
    protected ThemeManager themeManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        languageManager = new LanguageManager(this);
        themeManager = new ThemeManager(this);
        
        themeManager.applyTheme();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        LanguageManager lm = new LanguageManager(newBase);
        super.attachBaseContext(lm.updateResources(lm.getLanguage()));
    }
}
