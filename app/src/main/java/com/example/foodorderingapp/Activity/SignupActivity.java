package com.example.foodorderingapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.Helper.FirebaseManager;
import com.example.foodorderingapp.Helper.SessionManager;
import com.example.foodorderingapp.Model.UserProfile;
import com.example.foodorderingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends BaseActivity {
    private EditText userEdt, passEdt, nameEdt, phoneEdt;
    private SessionManager sessionManager;
    private FirebaseManager firebaseManager;
    private ProgressBar progressBar;
    private View progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sessionManager = new SessionManager(this);
        firebaseManager = FirebaseManager.getInstance();
        initView();
    }

    private void initView() {
        nameEdt = findViewById(R.id.nameEdt);
        userEdt = findViewById(R.id.userEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        passEdt = findViewById(R.id.passEdt);
        progressBar = findViewById(R.id.progressBar);
        progressOverlay = findViewById(R.id.progressOverlay);

        findViewById(R.id.signupBtn).setOnClickListener(v -> {
            String name = nameEdt.getText().toString().trim();
            String email = userEdt.getText().toString().trim();
            String phone = phoneEdt.getText().toString().trim();
            String password = passEdt.getText().toString();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(SignupActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(SignupActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(SignupActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(SignupActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(SignupActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user with Firebase Auth
            showLoading(true);
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // Create user profile in Firestore
                        UserProfile userProfile = new UserProfile(user.getUid(), name, email, phone, "");

                        firebaseManager.createUserProfile(userProfile, new FirebaseManager.SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                showLoading(false);
                                // Save session
                                sessionManager.saveLoginSession(user.getUid(), email, true);

                                Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onFailure(String error) {
                                showLoading(false);
                                Toast.makeText(SignupActivity.this, "Failed to create profile: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        showLoading(false);
                    }
                } else {
                    showLoading(false);
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                    Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        findViewById(R.id.loginTxt).setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null && progressOverlay != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
