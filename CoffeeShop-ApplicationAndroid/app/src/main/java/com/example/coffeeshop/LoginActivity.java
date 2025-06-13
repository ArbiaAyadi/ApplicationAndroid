package com.example.coffeeshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, goToRegisterButton;
    private TextView errorTextView, forgotPasswordTextView;
    private ImageView eyePasswordImageView;
    private ProgressBar progressBarLogin;
    private CheckBox rememberMeCheckBox;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.editTextLoginEmail);
        passwordEditText = findViewById(R.id.editTextLoginPassword);
        loginButton = findViewById(R.id.buttonLogin);
        goToRegisterButton = findViewById(R.id.buttonGoToRegister);
        errorTextView = findViewById(R.id.textViewError);
        forgotPasswordTextView = findViewById(R.id.textViewForgotPassword);
        eyePasswordImageView = findViewById(R.id.imageViewEyePassword);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        rememberMeCheckBox = findViewById(R.id.checkBoxRememberMe);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("appPreferences", MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);

        if (rememberMe) {
            String email = sharedPreferences.getString("email", "");
            String password = sharedPreferences.getString("password", "");
            emailEditText.setText(email);
            passwordEditText.setText(password);
        }
        rememberMeCheckBox.setChecked(rememberMe);

        eyePasswordImageView.setOnClickListener(v -> togglePasswordVisibility(passwordEditText, eyePasswordImageView));

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        goToRegisterButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        forgotPasswordTextView.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBarLogin.setVisibility(View.VISIBLE);

            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid -> {
                        progressBarLogin.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressBarLogin.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void loginUser(String email, String password) {
        progressBarLogin.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBarLogin.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {

                            // Save or remove user credentials based on "Remember Me"
                            SharedPreferences.Editor editor = getSharedPreferences("appPreferences", MODE_PRIVATE).edit();
                            if (rememberMeCheckBox.isChecked()) {
                                editor.putBoolean("rememberMe", true);
                                editor.putString("email", email);
                                editor.putString("password", password);
                            } else {
                                editor.remove("rememberMe");
                                editor.remove("email");
                                editor.remove("password");
                            }
                            editor.apply();

                            db.collection("users").document(user.getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String role = documentSnapshot.getString("role");

                                            if ("admin".equals(role)) {
                                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                            } else {
                                                startActivity(new Intent(LoginActivity.this, CustomerActivity.class));
                                            }
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(LoginActivity.this, "Failed to fetch user role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(LoginActivity.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        errorTextView.setText(errorMessage);
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void togglePasswordVisibility(EditText passwordEditText, ImageView eyeImageView) {
        if (passwordEditText.getTransformationMethod() != null) {
            passwordEditText.setTransformationMethod(null);
            eyeImageView.setImageResource(R.drawable.eye_show);
        } else {
            passwordEditText.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
            eyeImageView.setImageResource(R.drawable.eye_hide);
        }
    }
}
