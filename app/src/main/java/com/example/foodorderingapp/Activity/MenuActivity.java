package com.example.foodorderingapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.Adapter.FoodGridAdapter;
import com.example.foodorderingapp.Helper.FirebaseManager;
import com.example.foodorderingapp.Model.Food;
import com.example.foodorderingapp.R;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private FoodGridAdapter adapter;
    private FirebaseManager firebaseManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        firebaseManager = FirebaseManager.getInstance();
        initView();
        loadAllFoods();

        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
    }

    private void initView() {
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.menuRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new FoodGridAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void loadAllFoods() {
        showLoading(true);
        firebaseManager.getAllFoods(new FirebaseManager.FoodsCallback() {
            @Override
            public void onSuccess(List<Food> foods) {
                showLoading(false);
                if (foods != null && !foods.isEmpty()) {
                    adapter.updateData(foods);
                } else {
                    Toast.makeText(MenuActivity.this, "No foods available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(MenuActivity.this, "Error loading menu: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
