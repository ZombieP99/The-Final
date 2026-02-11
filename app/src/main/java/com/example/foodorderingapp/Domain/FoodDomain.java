package com.example.foodorderingapp.Domain;

import java.io.Serializable;

public class FoodDomain implements Serializable {
    private String title;
    private String picUrl;
    private String description;
    private double fee;
    private int numberInCart;

    public FoodDomain(String title, String picUrl, String description, double fee) {
        this.title = title;
        this.picUrl = picUrl;
        this.description = description;
        this.fee = fee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }
}
