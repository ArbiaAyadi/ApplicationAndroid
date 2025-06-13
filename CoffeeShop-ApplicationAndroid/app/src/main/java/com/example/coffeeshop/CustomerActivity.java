package com.example.coffeeshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class CustomerActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCoffee;
    private CustomerAdapter customerAdapter;
    private List<CoffeeModel> coffeeList = new ArrayList<>();
    private List<CoffeeModel> cartItems = new ArrayList<>();

    FirebaseFirestore db;
    ImageView imageViewCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        imageViewCart = findViewById(R.id.imageViewCart);

        recyclerViewCoffee = findViewById(R.id.recyclerViewCoffee);
        customerAdapter = new CustomerAdapter(this, new ArrayList<>(), cartItems);
        recyclerViewCoffee.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCoffee.setAdapter(customerAdapter);

        db = FirebaseFirestore.getInstance();

        fetchCoffeeTable();

        imageViewCart.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this, CartActivity.class);
            intent.putExtra("cartItems", new ArrayList<>(cartItems));
            startActivity(intent);
        });

    }

    private void fetchCoffeeTable() {
        db.collection("coffeeTable")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        coffeeList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            CoffeeModel coffee = document.toObject(CoffeeModel.class);
                            coffeeList.add(coffee);
                        }
                        customerAdapter.updateData(new ArrayList<>(coffeeList), cartItems);
                    }
                });
    }
}
