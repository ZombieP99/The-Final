package com.example.foodorderingapp.Helper;

import android.util.Log;

import com.example.foodorderingapp.Model.Category;
import com.example.foodorderingapp.Model.Food;
import com.example.foodorderingapp.Model.Order;
import com.example.foodorderingapp.Model.UserProfile;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private static FirebaseManager instance;
    private FirebaseFirestore db;

    // Collection names
    private static final String COLLECTION_CATEGORIES = "categories";
    private static final String COLLECTION_FOODS = "foods";
    private static final String COLLECTION_ORDERS = "orders";
    private static final String COLLECTION_USERS = "users";

    private FirebaseManager() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // Categories Methods
    public interface CategoriesCallback {
        void onSuccess(List<Category> categories);
        void onFailure(String error);
    }

    public void getCategories(CategoriesCallback callback) {
        db.collection(COLLECTION_CATEGORIES)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Category> categories = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Category category = document.toObject(Category.class);
                        categories.add(category);
                    }
                    callback.onSuccess(categories);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting categories", e);
                    callback.onFailure(e.getMessage());
                });
    }

    // Foods Methods
    public interface FoodsCallback {
        void onSuccess(List<Food> foods);
        void onFailure(String error);
    }

    public void getAllFoods(FoodsCallback callback) {
        db.collection(COLLECTION_FOODS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Food> foods = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Food food = document.toObject(Food.class);
                        foods.add(food);
                    }
                    callback.onSuccess(foods);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting foods", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void getFoodsByCategory(String category, FoodsCallback callback) {
        db.collection(COLLECTION_FOODS)
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Food> foods = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Food food = document.toObject(Food.class);
                        foods.add(food);
                    }
                    callback.onSuccess(foods);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting foods by category", e);
                    callback.onFailure(e.getMessage());
                });
    }

    // Orders Methods
    public interface OrderCallback {
        void onSuccess(String orderId);
        void onFailure(String error);
    }

    public interface OrdersCallback {
        void onSuccess(List<Order> orders);
        void onFailure(String error);
    }

    public void createOrder(Order order, OrderCallback callback) {
        db.collection(COLLECTION_ORDERS)
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Order created with ID: " + documentReference.getId());
                    callback.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating order", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void getUserOrders(String userId, OrdersCallback callback) {
        db.collection(COLLECTION_ORDERS)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Order order = document.toObject(Order.class);
                        orders.add(order);
                    }
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting user orders", e);
                    callback.onFailure(e.getMessage());
                });
    }

    // User Profile Methods
    public interface UserProfileCallback {
        void onSuccess(UserProfile userProfile);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void getUserProfile(String userId, UserProfileCallback callback) {
        db.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                        callback.onSuccess(userProfile);
                    } else {
                        callback.onFailure("User profile not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting user profile", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void createUserProfile(UserProfile userProfile, SimpleCallback callback) {
        db.collection(COLLECTION_USERS)
                .document(userProfile.getId())
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User profile created successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating user profile", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void updateUserProfile(UserProfile userProfile, SimpleCallback callback) {
        db.collection(COLLECTION_USERS)
                .document(userProfile.getId())
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User profile updated successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user profile", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(String error);
    }

    public void uploadProfileImage(android.net.Uri fileUri, String userId, UploadCallback callback) {
        com.google.firebase.storage.FirebaseStorage storage = com.google.firebase.storage.FirebaseStorage.getInstance();
        com.google.firebase.storage.StorageReference storageRef = storage.getReference()
                .child("profile_images/" + userId + ".jpg");

        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        // Update Firestore with the new image URL
                        db.collection(COLLECTION_USERS)
                                .document(userId)
                                .update("imageUrl", imageUrl)
                                .addOnSuccessListener(aVoid -> callback.onSuccess(imageUrl))
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error uploading image", e);
                    callback.onFailure(e.getMessage());
                });
    }
}
