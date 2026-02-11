package com.example.foodorderingapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.Adapter.OrderAdapter;
import com.example.foodorderingapp.Helper.FirebaseManager;
import com.example.foodorderingapp.Model.Order;
import com.example.foodorderingapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class OrdersActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private TextView emptyTxt;
    private FirebaseManager firebaseManager;
    private ProgressBar progressBar;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        firebaseManager = FirebaseManager.getInstance();
        initView();
        loadOrders();
    }

    private void initView() {
        recyclerView = findViewById(R.id.ordersRecyclerView);
        emptyTxt = findViewById(R.id.emptyTxT);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
    }

    private void loadOrders() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            finish();
            return;
        }

        showLoading(true);
        firebaseManager.getUserOrders(userId, new FirebaseManager.OrdersCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                showLoading(false);
                if (orders == null || orders.isEmpty()) {
                    emptyTxt.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyTxt.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new OrderAdapter(orders);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(OrdersActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
