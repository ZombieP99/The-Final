package com.example.foodorderingapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.Helper.FirebaseManager;
import com.example.foodorderingapp.Helper.ManagementCart;
import com.example.foodorderingapp.Model.Food;
import com.example.foodorderingapp.Model.Order;
import com.example.foodorderingapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class CheckoutActivity extends BaseActivity {
    private EditText addressEdt, phoneEdt;
    private TextView totalItemPriceTxt, totalPriceTxt;
    private ManagementCart managementCart;
    private FirebaseManager firebaseManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        managementCart = new ManagementCart(this);
        firebaseManager = FirebaseManager.getInstance();

        initView();
        calculateSummary();
    }

    private void initView() {
        addressEdt = findViewById(R.id.addressEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        totalItemPriceTxt = findViewById(R.id.totalItemPriceTxt);
        totalPriceTxt = findViewById(R.id.totalPriceTxt);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
        
        findViewById(R.id.checkoutBtn).setOnClickListener(v -> placeOrder());
    }

    private void calculateSummary() {
        double fee = managementCart.getTotalFee();
        double tax = Math.round((fee * 0.02) * 100) / 100.0;
        double delivery = 10;
        double total = Math.round((fee + tax + delivery) * 100) / 100.0;

        totalItemPriceTxt.setText("$" + fee);
        totalPriceTxt.setText("$" + total);
    }

    private void placeOrder() {
        String address = addressEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();

        if (address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Food> items = managementCart.getListCart();
        if (items.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getUid();
        double total = Double.parseDouble(totalPriceTxt.getText().toString().replace("$", ""));

        Order order = new Order();
        order.setUserId(userId);
        order.setItems(items);
        order.setTotalPrice(total);
        order.setDeliveryAddress(address);
        order.setPhoneNumber(phone);
        order.setStatus("Pending");
        order.setOrderDate(Timestamp.now());

        showLoading(true);
        firebaseManager.createOrder(order, new FirebaseManager.OrderCallback() {
            @Override
            public void onSuccess(String orderId) {
                showLoading(false);
                Toast.makeText(CheckoutActivity.this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                
                // Clear cart
                managementCart.saveCart(new ArrayList<>());
                
                // Go to home and clear task
                Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(CheckoutActivity.this, "Failed to place order: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
