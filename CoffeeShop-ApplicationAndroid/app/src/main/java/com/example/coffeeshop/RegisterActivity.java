package com.example.coffeeshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton, loginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView errorTextView;
    private ImageView eyePasswordImageView, eyeConfirmPasswordImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        registerButton = findViewById(R.id.buttonRegister);
        loginButton = findViewById(R.id.buttonLogin);
        errorTextView = findViewById(R.id.textViewError);
        eyePasswordImageView = findViewById(R.id.imageViewEyePassword);
        eyeConfirmPasswordImageView = findViewById(R.id.imageViewEyeConfirmPassword);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        eyePasswordImageView.setOnClickListener(v -> togglePasswordVisibility(passwordEditText, eyePasswordImageView));
        eyeConfirmPasswordImageView.setOnClickListener(v -> togglePasswordVisibility(confirmPasswordEditText, eyeConfirmPasswordImageView));

        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                errorTextView.setText("Passwords do not match");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            if (!email.endsWith("@gmail.com")) {
                errorTextView.setText("Please use a valid Gmail address");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnSuccessListener(aVoid -> {
                                            saveUserToFirestore(email);
                                            Toast.makeText(this, "Verification email sent. Please check your Gmail.", Toast.LENGTH_LONG).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            errorTextView.setText("Failed to send verification email: " + e.getMessage());
                                            errorTextView.setVisibility(View.VISIBLE);
                                        });
                            }
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                            errorTextView.setText(errorMessage);
                            errorTextView.setVisibility(View.VISIBLE);
                        }
                    });
        });

        loginButton.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private void saveUserToFirestore(String email) {
        String uid = mAuth.getCurrentUser().getUid();

        String role = email.equals("sabaezeybi114@gmail.com") ? "admin" : "customer";

        UserModel newUser = new UserModel(email, role);

        db.collection("users").document(uid)
                .set(newUser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "User registered. Verify your email before logging in.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
