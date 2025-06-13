package com.example.coffeeshop;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SugarManageActivity extends AppCompatActivity {

    private Button addButton;
    private RecyclerView recyclerView;
    private SugarAdapter adapter ;

    private List<SugarModel> sugarList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugar_manage);

        addButton = findViewById(R.id.buttonAddCoffee);
        recyclerView = findViewById(R.id.recyclerViewSugar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SugarAdapter(this, sugarList, this::showEditSugarDialog);

        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> showAddSugarDialog());

        loadSugarItems();
    }


    private void showAddSugarDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_sugar, null);
        EditText typeInput = dialogView.findViewById(R.id.editSugarType);
        EditText bagsInput = dialogView.findViewById(R.id.editSugarBags);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Add Sugar")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String type = typeInput.getText().toString().trim();
                    String bagsStr = bagsInput.getText().toString().trim();

                    if (TextUtils.isEmpty(type) || TextUtils.isEmpty(bagsStr)) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int bagCount = Integer.parseInt(bagsStr);
                    SugarModel item = new SugarModel(type, bagCount);

                    db.collection("sugarOptions").add(item)
                            .addOnSuccessListener(docRef -> {
                                Toast.makeText(this, "Sugar Added", Toast.LENGTH_SHORT).show();
                                loadSugarItems();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error adding sugar", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", null);
        builder.show();
    }



    public void showEditSugarDialog(SugarModel item) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_sugar, null);
        EditText typeInput = dialogView.findViewById(R.id.editSugarType);
        EditText bagsInput = dialogView.findViewById(R.id.editSugarBags);

        typeInput.setText(item.getType());
        bagsInput.setText(String.valueOf(item.getBagCount()));

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Edit Sugar")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String updatedType = typeInput.getText().toString().trim();
                    String updatedBagsStr = bagsInput.getText().toString().trim();

                    if (TextUtils.isEmpty(updatedType) || TextUtils.isEmpty(updatedBagsStr)) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int updatedBags = Integer.parseInt(updatedBagsStr);

                    item.setType(updatedType);
                    item.setBagCount(updatedBags);

                    db.collection("sugarOptions").document(item.getId())
                            .set(item)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Sugar Updated", Toast.LENGTH_SHORT).show();
                                loadSugarItems(); // Refresh list
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error updating sugar", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", null);

        builder.show();
    }






    private void loadSugarItems() {
        db.collection("sugarOptions").get()
                .addOnSuccessListener(snapshot -> {
                    sugarList.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        SugarModel item = doc.toObject(SugarModel.class);
                        item.setId(doc.getId());
                        sugarList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
