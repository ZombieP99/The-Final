package com.example.foodorderingapp.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.foodorderingapp.Model.Food;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ManagementCart {
    private Context context;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public ManagementCart(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("food_app", Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void insertFood(Food item) {
        ArrayList<Food> listCart = getListCart();
        boolean existsAlready = false;
        int n = 0;
        for (int i = 0; i < listCart.size(); i++) {
            if (listCart.get(i).getId().equals(item.getId())) {
                existsAlready = true;
                n = i;
                break;
            }
        }

        if (existsAlready) {
            listCart.get(n).setQuantity(item.getQuantity());
        } else {
            listCart.add(item);
        }
        
        saveCart(listCart);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<Food> getListCart() {
        String json = sharedPreferences.getString("CartList", null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Food>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveCart(ArrayList<Food> listCart) {
        String json = gson.toJson(listCart);
        sharedPreferences.edit().putString("CartList", json).apply();
    }

    public void minusNumberFood(ArrayList<Food> listCart, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        if (listCart.get(position).getQuantity() == 1) {
            listCart.remove(position);
        } else {
            listCart.get(position).setQuantity(listCart.get(position).getQuantity() - 1);
        }
        saveCart(listCart);
        changeNumberItemsListener.changed();
    }

    public void plusNumberFood(ArrayList<Food> listCart, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listCart.get(position).setQuantity(listCart.get(position).getQuantity() + 1);
        saveCart(listCart);
        changeNumberItemsListener.changed();
    }

    public Double getTotalFee() {
        ArrayList<Food> listCart = getListCart();
        double fee = 0;
        for (int i = 0; i < listCart.size(); i++) {
            fee = fee + (listCart.get(i).getPrice() * listCart.get(i).getQuantity());
        }
        return fee;
    }
}
