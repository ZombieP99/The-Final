package com.example.foodorderingapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.Helper.SessionManager;
import com.example.foodorderingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {
    private EditText userEdt, passEdt;
    private CheckBox rememberMeCheckbox;
    private SessionManager sessionManager;
    private ProgressBar progressBar;
    private View progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        initView();
    }

    private void initView() {
        userEdt = findViewById(R.id.userEdt);
        passEdt = findViewById(R.id.passEdt);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        progressBar = findViewById(R.id.progressBar);
        progressOverlay = findViewById(R.id.progressOverlay);
        TextView signUpText = findViewById(R.id.signUpText);

        // Login button click
        findViewById(R.id.loginBtn).setOnClickListener(v -> {
            String email = userEdt.getText().toString().trim();
            String password = passEdt.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Please fill your email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            showLoading(true);
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                showLoading(false);
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // Save session if Remember Me is checked
                        boolean rememberMe = rememberMeCheckbox.isChecked();
                        sessionManager.saveLoginSession(user.getUid(), email, rememberMe);

                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Sign Up text click
        signUpText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
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
