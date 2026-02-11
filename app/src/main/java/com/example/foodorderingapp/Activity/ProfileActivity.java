package com.example.foodorderingapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.Helper.FirebaseManager;
import com.example.foodorderingapp.Helper.SessionManager;
import com.example.foodorderingapp.Model.UserProfile;
import com.example.foodorderingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends BaseActivity {
    private SessionManager sessionManager;
    private FirebaseManager firebaseManager;
    private ImageView profilePic;
    private TextView nameTxt, emailTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        firebaseManager = FirebaseManager.getInstance();

        initViews();
        loadUserProfile();
        bottomNavigation();
    }

    private void initViews() {
        nameTxt = findViewById(R.id.nameTxt);
        emailTxt = findViewById(R.id.emailTxt);
        profilePic = findViewById(R.id.profilePic);

        profilePic.setOnClickListener(v -> {
            pickImage();
        });

        findViewById(R.id.logoutBtn).setOnClickListener(v -> {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Clear session
            sessionManager.clearSession();

            // Navigate to Login
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.ordersBtn).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, OrdersActivity.class));
        });

        findViewById(R.id.editProfileBtn).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });

        findViewById(R.id.settingsBtn).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
        });
    }

    private final androidx.activity.result.ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    android.net.Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        uploadImage(imageUri);
                    }
                }
            });

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadImage(android.net.Uri uri) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();

        firebaseManager.uploadProfileImage(uri, userId, new FirebaseManager.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                Toast.makeText(ProfileActivity.this, "Profile image updated!", Toast.LENGTH_SHORT).show();
                com.bumptech.glide.Glide.with(ProfileActivity.this)
                        .load(imageUrl)
                        .transform(new com.bumptech.glide.load.resource.bitmap.CenterCrop(), new com.bumptech.glide.load.resource.bitmap.RoundedCorners(100))
                        .into(profilePic);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ProfileActivity.this, "Upload failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Set email from Firebase Auth
            emailTxt.setText(currentUser.getEmail());

            // Load profile from Firestore
            firebaseManager.getUserProfile(currentUser.getUid(), new FirebaseManager.UserProfileCallback() {
                @Override
                public void onSuccess(UserProfile userProfile) {
                    if (userProfile != null) {
                        nameTxt.setText(userProfile.getName());
                        if (userProfile.getImageUrl() != null && !userProfile.getImageUrl().isEmpty()) {
                            com.bumptech.glide.Glide.with(ProfileActivity.this)
                                    .load(userProfile.getImageUrl())
                                    .transform(new com.bumptech.glide.load.resource.bitmap.CenterCrop(), new com.bumptech.glide.load.resource.bitmap.RoundedCorners(100))
                                    .into(profilePic);
                        }
                    } else {
                        nameTxt.setText("User");
                    }
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(ProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    nameTxt.setText("User");
                }
            });
        } else {
            // No user logged in, redirect to login
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void bottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.support_menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home_menu) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.cart_menu) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.support_menu) {
                return true;
            }
            return false;
        });
    }
}
