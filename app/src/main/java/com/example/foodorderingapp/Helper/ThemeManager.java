package com.example.foodorderingapp.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    private Context context;
    private SharedPreferences sharedPreferences;

    public ThemeManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("food_app_prefs", Context.MODE_PRIVATE);
    }

    public void setTheme(boolean isDarkMode) {
        sharedPreferences.edit().putBoolean("dark_mode", isDarkMode).apply();
        applyTheme();
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean("dark_mode", false);
    }

    public void applyTheme() {
        if (isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
