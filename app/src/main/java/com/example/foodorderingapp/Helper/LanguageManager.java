package com.example.foodorderingapp.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageManager {
    private Context context;
    private SharedPreferences sharedPreferences;

    public LanguageManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("food_app_prefs", Context.MODE_PRIVATE);
    }

    public void setLocale(String languageCode) {
        sharedPreferences.edit().putString("language", languageCode).apply();
        updateResources(languageCode);
    }

    public String getLanguage() {
        return sharedPreferences.getString("language", "en");
    }

    public Context updateResources(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }
}
