package com.example.coffeeshop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CoffeeManageActivity extends AppCompatActivity {

    private Button addButton;
    private RecyclerView recyclerView;
    private CoffeeAdapter adapter;
    private List<CoffeeModel> coffeeList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Uri selectedImageUri;
    private ImageView imagePreview;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_manage);

        addButton = findViewById(R.id.buttonAddCoffee);
        recyclerView = findViewById(R.id.recyclerViewCoffee);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CoffeeAdapter(this, coffeeList, this::showEditCoffeeDialog);


        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> showAddCoffeeDialog());

        loadCoffeeTable();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (imagePreview != null && selectedImageUri != null) {
                            imagePreview.setImageURI(selectedImageUri);
                        }
                    }
                }
        );
    }

    private void showAddCoffeeDialog() {
        selectedImageUri = null;

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_coffee, null);
        EditText nameInput = dialogView.findViewById(R.id.editCoffeeName);
        EditText descInput = dialogView.findViewById(R.id.editCoffeeDesc);
        EditText priceInput = dialogView.findViewById(R.id.editCoffeePrice);
        imagePreview = dialogView.findViewById(R.id.imagePreview);
        Button chooseImage = dialogView.findViewById(R.id.buttonChooseImage);

        chooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Coffee");
        builder.setView(dialogView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String description = descInput.getText().toString().trim();
            String priceStr = priceInput.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceStr)) {
                Toast.makeText(CoffeeManageActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            CoffeeModel newCoffee = new CoffeeModel(name, description, price  );

            if (selectedImageUri != null) {
                try {
                    String base64Image = encodeImageToBase64(selectedImageUri);
                    newCoffee.setImageBase64(base64Image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            db.collection("coffeeTable")
                    .add(newCoffee)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(CoffeeManageActivity.this, "Coffee Added!", Toast.LENGTH_SHORT).show();
                        loadCoffeeTable();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CoffeeManageActivity.this, "Error adding coffee", Toast.LENGTH_SHORT).show();
                    });
        });


        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    public void showEditCoffeeDialog(CoffeeModel coffee) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_coffee, null);

        EditText nameInput = dialogView.findViewById(R.id.editCoffeeName);
        EditText descInput = dialogView.findViewById(R.id.editCoffeeDesc);
        EditText priceInput = dialogView.findViewById(R.id.editCoffeePrice);
        imagePreview = dialogView.findViewById(R.id.imagePreview);
        Button chooseImage = dialogView.findViewById(R.id.buttonChooseImage);

        nameInput.setText(coffee.getName());
        descInput.setText(coffee.getDescription());
        priceInput.setText(String.valueOf(coffee.getPrice()));

        if (coffee.getImageBase64() != null && !coffee.getImageBase64().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(coffee.getImageBase64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imagePreview.setImageBitmap(decodedByte);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        chooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Edit Coffee")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String description = descInput.getText().toString().trim();
                    String priceStr = priceInput.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceStr)) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price = Double.parseDouble(priceStr);

                    coffee.setName(name);
                    coffee.setDescription(description);
                    coffee.setPrice(price);

                    if (selectedImageUri != null) {
                        try {
                            String base64Image = encodeImageToBase64(selectedImageUri);  // Convert image to Base64
                            coffee.setImageBase64(base64Image);  // Store Base64 string
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    db.collection("coffeeTable")
                            .document(coffee.getId())
                            .set(coffee)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Coffee updated", Toast.LENGTH_SHORT).show();
                                loadCoffeeTable();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }



    private String encodeImageToBase64(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    private void loadCoffeeTable() {
        db.collection("coffeeTable")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    coffeeList.clear();
                    for (QueryDocumentSnapshot document: queryDocumentSnapshots) {
                        CoffeeModel coffee = document.toObject(CoffeeModel.class);
                        coffee.setId(document.getId());
                        coffeeList.add(coffee);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CoffeeManageActivity.this, "Error loading coffee", Toast.LENGTH_SHORT).show();
                });
    }
}
