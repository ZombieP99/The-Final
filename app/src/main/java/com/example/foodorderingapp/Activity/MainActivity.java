package com.example.foodorderingapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodorderingapp.Adapter.CategoryAdapterNew;
import com.example.foodorderingapp.Adapter.FoodAdapter;
import com.example.foodorderingapp.Helper.DatabaseSeeder;
import com.example.foodorderingapp.Helper.FirebaseManager;
import com.example.foodorderingapp.Model.Category;
import com.example.foodorderingapp.Model.Food;
import com.example.foodorderingapp.Model.UserProfile;
import com.example.foodorderingapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerViewCategoryList, recyclerViewPopularList;
    private CategoryAdapterNew categoryAdapter;
    private FoodAdapter foodAdapter;
    private FirebaseManager firebaseManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseManager = FirebaseManager.getInstance();
        initViews();
        
        // تشغيل DatabaseSeeder لأول مرة
        seedDatabaseIfNeeded();
        
        loadCategories();
        loadPopularFoods();
        loadUserProfile();
        bottomNavigation();

        findViewById(R.id.viewAllBtn).setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, MenuActivity.class));
        });
    }

    private void loadUserProfile() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        firebaseManager.getUserProfile(userId, new FirebaseManager.UserProfileCallback() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                TextView greetingTxt = findViewById(R.id.greetingText);
                ImageView profileImg = findViewById(R.id.profileImage);

                if (userProfile.getName() != null) {
                    greetingTxt.setText("Hi " + userProfile.getName() + " 👋");
                }

                if (userProfile.getImageUrl() != null && !userProfile.getImageUrl().isEmpty()) {
                    Glide.with(MainActivity.this)
                            .load(userProfile.getImageUrl())
                            .transform(new CenterCrop(), new RoundedCorners(24))
                            .into(profileImg);
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Error fetching profile: " + error);
            }
        });
    }

    private void seedDatabaseIfNeeded() {
        DatabaseSeeder seeder = new DatabaseSeeder(this);
        if (!seeder.isDataSeeded()) {
            Log.d(TAG, "First run - seeding database...");
            showLoading(true);
            seeder.seedDatabase(new DatabaseSeeder.SeedCallback() {
                @Override
                public void onComplete() {
                    Log.d(TAG, "Database seeded successfully!");
                    showLoading(false);
                    // إعادة تحميل البيانات بعد الإضافة
                    loadCategories();
                    loadPopularFoods();
                    Toast.makeText(MainActivity.this, "Welcome! Data loaded successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error seeding database: " + error);
                    showLoading(false);
                    Toast.makeText(MainActivity.this, "Error loading initial data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        recyclerViewCategoryList = findViewById(R.id.viewCategory);
        recyclerViewPopularList = findViewById(R.id.viewPopular);

        // Setup Categories RecyclerView
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategoryList.setLayoutManager(categoryLayoutManager);
        categoryAdapter = new CategoryAdapterNew(new ArrayList<>(), category -> {
            // Handle category click - filter foods by category
            loadFoodsByCategory(category.getName());
        });
        recyclerViewCategoryList.setAdapter(categoryAdapter);

        // Setup Popular Foods RecyclerView
        LinearLayoutManager foodLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPopularList.setLayoutManager(foodLayoutManager);
        foodAdapter = new FoodAdapter(new ArrayList<>());
        recyclerViewPopularList.setAdapter(foodAdapter);
    }

    private void loadCategories() {
        firebaseManager.getCategories(new FirebaseManager.CategoriesCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                if (categories != null && !categories.isEmpty()) {
                    categoryAdapter.updateData(categories);
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Error loading categories: " + error);
            }
        });
    }

    private void loadPopularFoods() {
        firebaseManager.getAllFoods(new FirebaseManager.FoodsCallback() {
            @Override
            public void onSuccess(List<Food> foods) {
                if (foods != null && !foods.isEmpty()) {
                    foodAdapter.updateData(foods);
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Error loading foods: " + error);
            }
        });
    }

    private void loadFoodsByCategory(String categoryName) {
        showLoading(true);
        firebaseManager.getFoodsByCategory(categoryName, new FirebaseManager.FoodsCallback() {
            @Override
            public void onSuccess(List<Food> foods) {
                showLoading(false);
                if (foods != null && !foods.isEmpty()) {
                    foodAdapter.updateData(foods);
                } else {
                    Toast.makeText(MainActivity.this, "No foods in this category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(MainActivity.this, "Error loading foods: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void bottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.home_menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home_menu) {
                return true;
            } else if (itemId == R.id.cart_menu) {
                startActivity(new android.content.Intent(getApplicationContext(), CartActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.support_menu) {
                startActivity(new android.content.Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}
