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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CupsManageActivity extends AppCompatActivity {

    private Button addbtn;
    private RecyclerView recyclerView;
    private CupsAdapter adapter;
    private List<CupsModel> CupsList = new ArrayList<>();

    private Uri selectedImageUri;
    private ImageView imagePreview;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cups_manage);

        addbtn = findViewById(R.id.addcupid);
        recyclerView = findViewById(R.id.recyclerViewCups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CupsAdapter(this, CupsList, this::showEditCupsDialog);

        recyclerView.setAdapter(adapter);

        addbtn.setOnClickListener(v -> showAddCupDialog());

        loadCupItems();


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

    private void showAddCupDialog() {

        selectedImageUri = null;

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_cups, null);
        EditText nameInput = dialogView.findViewById(R.id.NameInput);
        EditText descriptionInput = dialogView.findViewById(R.id.DescriptionInput);
        EditText sizeInput = dialogView.findViewById(R.id.SizeInput);
        EditText stockInput = dialogView.findViewById(R.id.StockInput);
        imagePreview = dialogView.findViewById(R.id.cupImageView);
        Button chooseImage = dialogView.findViewById(R.id.buttonChooseImage);


        chooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Add Cup")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String description = descriptionInput.getText().toString().trim();
                    String size = sizeInput.getText().toString().trim();
                    String stock = stockInput.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(size) || TextUtils.isEmpty(stock)) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int stockInt = Integer.parseInt(stock);

                    CupsModel newCup = new CupsModel(name, description, size, stockInt);
                    if (selectedImageUri != null) {
                        try {
                            String base64Image = encodeImageToBase64(selectedImageUri);
                            newCup.setImageBase64(base64Image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    db.collection("CupsTable").add(newCup)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(this, "Cup Added", Toast.LENGTH_SHORT).show();
                                loadCupItems();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error adding Cup", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", null);

        builder.show();
    }

    private void showEditCupsDialog(CupsModel cup) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_cups, null);
        EditText nameInput = dialogView.findViewById(R.id.NameInput);
        EditText descriptionInput = dialogView.findViewById(R.id.DescriptionInput);
        EditText sizeInput = dialogView.findViewById(R.id.SizeInput);
        EditText stockInput = dialogView.findViewById(R.id.StockInput);
        imagePreview = dialogView.findViewById(R.id.cupImageView);
        Button chooseImage = dialogView.findViewById(R.id.buttonChooseImage);


        nameInput.setText(cup.getName());
        descriptionInput.setText(cup.getDescription());
        sizeInput.setText(cup.getSize());
        stockInput.setText(String.valueOf(cup.getStock()));
        if (cup.getImageBase64() != null && !cup.getImageBase64().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(cup.getImageBase64(), Base64.DEFAULT);
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
                .setTitle("Edit Cup")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String updatedName = nameInput.getText().toString().trim();
                    String updatedDescription = descriptionInput.getText().toString().trim();
                    String updatedSize = sizeInput.getText().toString().trim().toLowerCase();
                    String updatedStock = stockInput.getText().toString().trim();

                    if (TextUtils.isEmpty(updatedName) || TextUtils.isEmpty(updatedDescription) ||
                            TextUtils.isEmpty(updatedSize) || TextUtils.isEmpty(updatedStock)) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!updatedSize.equals("small") && !updatedSize.equals("medium") && !updatedSize.equals("large")) {
                        Toast.makeText(this, "Size must be small, medium, or large", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int updatedStockInt = Integer.parseInt(updatedStock);

                    cup.setName(updatedName);
                    cup.setDescription(updatedDescription);
                    cup.setSize(updatedSize);
                    cup.setStock(updatedStockInt);
                    if (selectedImageUri != null) {
                        try {
                            String base64Image = encodeImageToBase64(selectedImageUri);  // Convert image to Base64
                            cup.setImageBase64(base64Image);  // Store Base64 string
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }



                    db.collection("CupsTable").document(cup.getId())
                            .set(cup)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Cup Updated", Toast.LENGTH_SHORT).show();
                                loadCupItems();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error updating Cup", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", null);

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
    private void loadCupItems() {
        db.collection("CupsTable").get().addOnSuccessListener(snapshot -> {
            CupsList.clear();
            for (QueryDocumentSnapshot doc : snapshot) {
                CupsModel cup = doc.toObject(CupsModel.class);
                cup.setId(doc.getId());
                CupsList.add(cup);
            }
            adapter.notifyDataSetChanged();
        })
                .addOnFailureListener(e -> {
                    Toast.makeText(CupsManageActivity.this, "Error loading cups", Toast.LENGTH_SHORT).show();
                });
    }

}
