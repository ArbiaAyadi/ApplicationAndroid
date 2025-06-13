package com.example.coffeeshop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CupsAdapter extends RecyclerView.Adapter<CupsAdapter.CupViewHolder> {

    private Context context;
    private List<CupsModel> cupList;
    private OnEditClickListener editClickListener;

    @FunctionalInterface
    public interface OnEditClickListener {
        void onEditClick(CupsModel cup);
    }

    public CupsAdapter(Context context, List<CupsModel> cupList, OnEditClickListener editClickListener) {
        this.context = context;
        this.cupList = cupList;
        this.editClickListener = editClickListener;
    }

    @NonNull
    @Override
    public CupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.display_cups, parent, false);
        return new CupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CupViewHolder holder, int position) {
        CupsModel cup = cupList.get(position);

        holder.name.setText(cup.getName());
        holder.description.setText(cup.getDescription());
        holder.size.setText("Size: " + cup.getSize());
        holder.stock.setText("Stock: " + cup.getStock());

        // Set click listener for the Edit button
        holder.editButton.setOnClickListener(v -> editClickListener.onEditClick(cup));

        // Set click listener for the Delete button
        holder.deleteButton.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("CupsTable").document(cup.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        cupList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cupList.size());
                    });
        });

        if (cup.getImageBase64() != null) {
            try {
                byte[] decodedString = Base64.decode(cup.getImageBase64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.image.setImageBitmap(decodedByte);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return cupList.size();
    }

    public static class CupViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, size, stock;
        Button editButton, deleteButton;

        ImageView image;

        public CupViewHolder(@NonNull View cupView) {
            super(cupView);
            name = cupView.findViewById(R.id.textNameView);
            description = cupView.findViewById(R.id.textDescriptionView);
            size = cupView.findViewById(R.id.textSizeView);
            stock = cupView.findViewById(R.id.textStockView);
            editButton = cupView.findViewById(R.id.buttonEditCup);
            deleteButton = cupView.findViewById(R.id.buttonDeleteCup);
            image = cupView.findViewById(R.id.cupImageView);

        }
    }

}