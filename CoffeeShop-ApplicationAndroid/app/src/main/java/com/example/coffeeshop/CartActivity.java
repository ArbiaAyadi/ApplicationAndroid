package com.example.coffeeshop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;


public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CoffeeModel> cartItems;
    private TextView textViewTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        textViewTotal = findViewById(R.id.textViewTotal);

        if (getIntent().hasExtra("cartItems")) {
            cartItems = (List<CoffeeModel>) getIntent().getSerializableExtra("cartItems");
        } else {
            cartItems = new ArrayList<>();
        }

        cartAdapter = new CartAdapter(this, cartItems, new CartAdapter.OnCartChangedListener() {
            @Override
            public void onCartChanged() {
                updateTotal();
            }

            @Override
            public void onSugarSelectionRequested(int position, CoffeeModel item) {
                showSugarSelectionDialog(position, item);
            }

            @Override
            public void onCupSelectionRequested(int position, CoffeeModel item) {
                showCupSelectionDialog(position, item);
            }
        });

        recyclerView.setAdapter(cartAdapter);
        updateTotal();

        Button buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(v -> showConfirmationDialog());
    }



    public void updateTotal() {
        double total = 0.0;
        for (CoffeeModel item : cartItems) {
            total += item.getPrice();
        }
        textViewTotal.setText("Total: $" + String.format("%.2f", total));
    }

    private void showConfirmationDialog() {

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirmation, null);

        EditText editTextAddress = dialogView.findViewById(R.id.editTextAddress);
        EditText editTextPhone = dialogView.findViewById(R.id.editTextPhone);
        Button buttonCard = dialogView.findViewById(R.id.buttonCard);
        Button buttonCheck = dialogView.findViewById(R.id.buttonCheck);
        Button buttonPass = dialogView.findViewById(R.id.buttonPass);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        buttonCard.setOnClickListener(v -> {
            buttonCard.setBackgroundTintList(ContextCompat.getColorStateList(CartActivity.this, R.color.green));

            buttonCheck.setBackgroundTintList(ContextCompat.getColorStateList(CartActivity.this, R.color.black));

            Toast.makeText(this, "Payment via Card", Toast.LENGTH_SHORT).show();
        });

        buttonCheck.setOnClickListener(v -> {
            buttonCheck.setBackgroundTintList(ContextCompat.getColorStateList(CartActivity.this, R.color.green));
            buttonCard.setBackgroundTintList(ContextCompat.getColorStateList(CartActivity.this, R.color.black));

            Toast.makeText(this, "Payment via Check", Toast.LENGTH_SHORT).show();
        });

        buttonPass.setOnClickListener(v -> {
            dialog.dismiss();
        });


        dialog.show();
    }


    private void showSugarSelectionDialog(int position, CoffeeModel coffeeModel) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_select_sugar, null);

        EditText editTextSugarCount = dialogView.findViewById(R.id.editTextSugarCount);
        RadioButton radioWhite = dialogView.findViewById(R.id.radioWhite);
        RadioButton radioBrown = dialogView.findViewById(R.id.radioBrown);
        Button buttonSave = dialogView.findViewById(R.id.buttonSaveSugar);

        if (coffeeModel.getSugar() != null) {
            SugarModel currentSugar = coffeeModel.getSugar();
            editTextSugarCount.setText(String.valueOf(currentSugar.getBagCount()));
            if ("White".equals(currentSugar.getType())) {
                radioWhite.setChecked(true);
            } else if ("Brown".equals(currentSugar.getType())) {
                radioBrown.setChecked(true);
            }
            buttonSave.setText("Update Sugar");
        } else {
            buttonSave.setText("Add Sugar");
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        buttonSave.setOnClickListener(v -> {
            String countText = editTextSugarCount.getText().toString().trim();
            if (countText.isEmpty()) {
                editTextSugarCount.setError("Enter sugar count");
                return;
            }

            String sugarType = "";
            if (radioWhite.isChecked()) sugarType = "White";
            else if (radioBrown.isChecked()) sugarType = "Brown";
            else {
                Toast.makeText(this, "Select sugar type", Toast.LENGTH_SHORT).show();
                return;
            }

            int sugarCount;
            try {
                sugarCount = Integer.parseInt(countText);
            } catch (NumberFormatException e) {
                editTextSugarCount.setError("Invalid number");
                return;
            }

            SugarModel sugarModel = new SugarModel(sugarType, sugarCount);
            coffeeModel.setSugar(sugarModel);  // Update the sugar for the coffee item
            cartAdapter.notifyItemChanged(position);  // Notify adapter to update the UI

            dialog.dismiss();
        });

        dialog.show();
    }

    private void showCupSelectionDialog(int position, CoffeeModel item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_cup, null);
        builder.setView(view);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CupSelectAdapter cupAdapter = new CupSelectAdapter(this, new ArrayList<>(), new CupSelectAdapter.OnCupSelectedListener() {
            @Override
            public void onCupSelected(CupsModel selectedCup) {
                item.setCup(selectedCup);
            }
        });

        recyclerView.setAdapter(cupAdapter);

        fetchCupsFromFirestore(cupAdapter, item);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            CupsModel selectedCup = item.getCup();

            if (selectedCup != null) {
                item.setCup(selectedCup);
                cartAdapter.notifyDataSetChanged();
            }

            Toast.makeText(CartActivity.this, "Cup Selected: " + selectedCup.getName(), Toast.LENGTH_SHORT).show();
        });


        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    private void fetchCupsFromFirestore(CupSelectAdapter cupAdapter, CoffeeModel coffeeModel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("CupsTable")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<CupsModel> cups = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            CupsModel cup = document.toObject(CupsModel.class);
                            cups.add(cup);
                        }
                        cupAdapter.updateCups(cups);

                        if (coffeeModel.getCup() != null) {
                        }

                    } else {
                        Toast.makeText(CartActivity.this, "No cups found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching cups: " + e.getMessage());
                    Toast.makeText(CartActivity.this, "Error fetching cups: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    }
