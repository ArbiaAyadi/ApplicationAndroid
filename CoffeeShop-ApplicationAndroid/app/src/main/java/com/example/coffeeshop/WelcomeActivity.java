package com.example.coffeeshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button registerbtn , loginbtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        registerbtn = findViewById(R.id.registerBTN) ;

        loginbtn = findViewById(R.id.loginBTN);



        registerbtn.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class)));


        loginbtn.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, LoginActivity.class)));

    }
}
