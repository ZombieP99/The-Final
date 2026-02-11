package com.example.foodorderingapp.Model;

import com.google.firebase.firestore.DocumentId;
import java.io.Serializable;

public class Food implements Serializable {
    @DocumentId
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String category;
    private double price;
    private double star;
    private int quantity; // For cart usage

    // Empty constructor required for Firestore
    public Food() {}

    public Food(String name, String imageUrl, String description, double price, double star, boolean popular, String category) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.price = price;
        this.star = star;
        this.category = category;
        this.quantity = 1;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getStar() { return star; }
    public void setStar(double star) { this.star = star; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
