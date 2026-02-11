package com.example.foodorderingapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Helper.ManagementCart;
import com.example.foodorderingapp.Model.Food;
import com.example.foodorderingapp.R;

public class ShowDetailActivity extends BaseActivity {
    private TextView addToCartBtn;
    private TextView titleTxt, priceTxt, descriptionTxt, numberOrderTxt, totalPriceTxt;
    private ImageView plusBtn, minusBtn, picFood;
    private Food object;
    private int numberOrder = 1;
    private ManagementCart managementCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);

        managementCart = new ManagementCart(this);
        initView();
        getBundle();
    }

    private void getBundle() {
        object = (Food) getIntent().getSerializableExtra("object");
        if (object == null) {
            finish();
            return;
        }

        Glide.with(this)
                .load(object.getImageUrl())
                .into(picFood);

        titleTxt.setText(object.getName());
        priceTxt.setText("$" + object.getPrice());
        descriptionTxt.setText(object.getDescription());
        numberOrderTxt.setText(String.valueOf(numberOrder));
        calculateTotal();

        addToCartBtn.setOnClickListener(v -> {
            object.setQuantity(numberOrder);
            managementCart.insertFood(object);
            finish();
        });

        plusBtn.setOnClickListener(v -> {
            numberOrder = numberOrder + 1;
            numberOrderTxt.setText(String.valueOf(numberOrder));
            calculateTotal();
        });

        minusBtn.setOnClickListener(v -> {
            if (numberOrder > 1) {
                numberOrder = numberOrder - 1;
            }
            numberOrderTxt.setText(String.valueOf(numberOrder));
            calculateTotal();
        });

        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
    }

    private void calculateTotal() {
        double total = Math.round((numberOrder * object.getPrice()) * 100) / 100.0;
        totalPriceTxt.setText("$" + total);
    }

    private void initView() {
        addToCartBtn = findViewById(R.id.addToCartBtn);
        titleTxt = findViewById(R.id.titleTxt);
        priceTxt = findViewById(R.id.priceTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        numberOrderTxt = findViewById(R.id.numberOrderTxt);
        plusBtn = findViewById(R.id.plusBtn);
        minusBtn = findViewById(R.id.minusBtn);
        picFood = findViewById(R.id.foodPic);
        totalPriceTxt = findViewById(R.id.totalPriceTxt);
    }
}
