package com.example.foodorderingapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodorderingapp.Helper.SessionManager;
import com.example.foodorderingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.viewpager2.widget.ViewPager2;
import com.example.foodorderingapp.Adapter.OnboardingAdapter;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends BaseActivity {

    private SessionManager sessionManager;
    private ViewPager2 viewPager2;
    private OnboardingAdapter onboardingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sessionManager = new SessionManager(this);
        
        // Check if user is already logged in
        checkLoginStatus();
        
        setContentView(R.layout.activity_intro);

        setupOnboarding();
        
        findViewById(R.id.btnNext).setOnClickListener(v -> {
            if (viewPager2.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            } else {
                // If it was the last page, navigate to Login
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setupOnboarding() {
        List<OnboardingAdapter.OnboardingItem> onboardingItems = new ArrayList<>();
        
        onboardingItems.add(new OnboardingAdapter.OnboardingItem(
                "Find Your Favorite Food",
                "Discover the best food from over 1,000 restaurants and fast delivery to your doorstep",
                R.drawable.piza
        ));

        onboardingItems.add(new OnboardingAdapter.OnboardingItem(
                "Fast Delivery",
                "Fast food delivery to your home, office wherever you are",
                R.drawable.borger
        ));

        onboardingItems.add(new OnboardingAdapter.OnboardingItem(
                "Live Tracking",
                "Real time tracking of your food on the map after placed the order",
                R.drawable.piza
        ));

        viewPager2 = findViewById(R.id.viewPager2);
        onboardingAdapter = new OnboardingAdapter(onboardingItems);
        viewPager2.setAdapter(onboardingAdapter);

        new TabLayoutMediator(findViewById(R.id.tabLayout), viewPager2, (tab, position) -> {
            // No text for indicators
        }).attach();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                android.widget.Button btnNext = findViewById(R.id.btnNext);
                if (position == onboardingAdapter.getItemCount() - 1) {
                    btnNext.setText("Get Started");
                } else {
                    btnNext.setText("Next");
                }
            }
        });
    }
    
    private void checkLoginStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        
        // If user is logged in with Firebase OR session is saved
        if (currentUser != null && sessionManager.isLoggedIn()) {
            // Go directly to main activity
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
        }
    }
}
