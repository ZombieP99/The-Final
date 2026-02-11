package com.example.foodorderingapp.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.foodorderingapp.Model.Category;
import com.example.foodorderingapp.Model.Food;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DatabaseSeeder {
    private static final String TAG = "DatabaseSeeder";
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_DATA_SEEDED = "dataSeeded";
    
    private FirebaseFirestore db;
    private Context context;

    public DatabaseSeeder(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    public boolean isDataSeeded() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_DATA_SEEDED, false);
    }

    private void markDataAsSeeded() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_DATA_SEEDED, true).apply();
    }

    public void seedDatabase(final SeedCallback callback) {
        if (isDataSeeded()) {
            if (callback != null) callback.onComplete();
            return;
        }

        seedCategories(new SeedCallback() {
            @Override
            public void onComplete() {
                seedFoods(new SeedCallback() {
                    @Override
                    public void onComplete() {
                        markDataAsSeeded();
                        if (callback != null) callback.onComplete();
                    }

                    @Override
                    public void onError(String error) {
                        if (callback != null) callback.onError(error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (callback != null) callback.onError(error);
            }
        });
    }

    private void seedCategories(final SeedCallback callback) {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Pizza", "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400"));
        categories.add(new Category("Burger", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400"));
        categories.add(new Category("Sushi", "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=400"));
        categories.add(new Category("Pasta", "https://images.unsplash.com/photo-1473093226795-af9932fe5856?w=400"));
        categories.add(new Category("Drink", "https://images.unsplash.com/photo-1544145945-f904253db0ad?w=400"));

        final int[] count = {0};
        for (Category category : categories) {
            db.collection("categories").add(category).addOnCompleteListener(task -> {
                count[0]++;
                if (count[0] == categories.size()) callback.onComplete();
            });
        }
    }

    private void seedFoods(final SeedCallback callback) {
        List<Food> foods = new ArrayList<>();
        // Pizza category
        foods.add(new Food("Pepperoni Pizza", "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=800", "Delicious pepperoni pizza with extra cheese.", 12.0, 4.5, true, "Pizza"));
        foods.add(new Food("Margarita Pizza", "https://images.unsplash.com/photo-1574071318508-1cdbad80ad50?w=800", "Classic margarita pizza with fresh basil.", 10.0, 4.2, false, "Pizza"));

        // Burger category
        foods.add(new Food("Cheese Burger", "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=800", "Juicy cheese burger with lettuce and tomato.", 8.5, 4.8, true, "Burger"));
        foods.add(new Food("Double Burger", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800", "Beef double burger for the hungry.", 15.0, 4.9, true, "Burger"));

        // Drink category
        foods.add(new Food("Coca Cola", "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=800", "Refreshing cold cola.", 2.5, 4.0, false, "Drink"));

        final int[] count = {0};
        for (Food food : foods) {
            db.collection("foods").add(food).addOnCompleteListener(task -> {
                count[0]++;
                if (count[0] == foods.size()) callback.onComplete();
            });
        }
    }

    public interface SeedCallback {
        void onComplete();
        void onError(String error);
    }
}
