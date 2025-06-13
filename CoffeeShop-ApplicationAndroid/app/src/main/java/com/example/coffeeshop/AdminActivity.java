package com.example.coffeeshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;


public class AdminActivity extends AppCompatActivity {


    private Button coffeeManageBTN , sugarManageBTN ,cupsManageBTN , drinksManageBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        coffeeManageBTN = findViewById(R.id.coffeemanagebtn);
        sugarManageBTN = findViewById(R.id.sugarmanagebtn);
        cupsManageBTN = findViewById(R.id.cupsbtn);
        drinksManageBTN = findViewById(R.id.drinksbtn);

        coffeeManageBTN.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, CoffeeManageActivity.class)));

        sugarManageBTN.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, SugarManageActivity.class)));

        cupsManageBTN.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, CupsManageActivity.class)));

    }
}

