package com.example.foodorderingapp.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodorderingapp.Helper.FirebaseManager;
import com.example.foodorderingapp.Model.UserProfile;
import com.example.foodorderingapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class EditProfileActivity extends BaseActivity {
    private EditText nameEdt, emailEdt, phoneEdt, imageUrlEdt;
    private ImageView profileImg;
    private ProgressBar progressBar;
    private FirebaseManager firebaseManager;
    private String userId;
    private UserProfile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseManager = FirebaseManager.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        initView();
        loadUserProfile();
    }

    private void initView() {
        nameEdt = findViewById(R.id.nameEdt);
        emailEdt = findViewById(R.id.emailEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        imageUrlEdt = findViewById(R.id.imageUrlEdt);
        profileImg = findViewById(R.id.editProfileImage);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
        findViewById(R.id.saveBtn).setOnClickListener(v -> saveProfile());

        imageUrlEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String url = s.toString().trim();
                if (!url.isEmpty()) {
                    Glide.with(EditProfileActivity.this)
                            .load(url)
                            .placeholder(R.drawable.gradient_primary)
                            .transform(new CenterCrop(), new RoundedCorners(60))
                            .into(profileImg);
                }
            }
        });
    }

    private void loadUserProfile() {
        if (userId == null) return;

        showLoading(true);
        firebaseManager.getUserProfile(userId, new FirebaseManager.UserProfileCallback() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                showLoading(false);
                currentProfile = userProfile;
                nameEdt.setText(userProfile.getName());
                emailEdt.setText(userProfile.getEmail());
                phoneEdt.setText(userProfile.getPhone());
                imageUrlEdt.setText(userProfile.getImageUrl());
                
                if (userProfile.getImageUrl() != null && !userProfile.getImageUrl().isEmpty()) {
                    Glide.with(EditProfileActivity.this)
                            .load(userProfile.getImageUrl())
                            .transform(new CenterCrop(), new RoundedCorners(60))
                            .into(profileImg);
                }
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(EditProfileActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String name = nameEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();
        String imageUrl = imageUrlEdt.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentProfile == null) return;

        currentProfile.setName(name);
        currentProfile.setPhone(phone);
        currentProfile.setImageUrl(imageUrl);

        showLoading(true);
        firebaseManager.updateUserProfile(currentProfile, new FirebaseManager.SimpleCallback() {
            @Override
            public void onSuccess() {
                showLoading(false);
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(EditProfileActivity.this, "Failed to update: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
