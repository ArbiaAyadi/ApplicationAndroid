package com.example.coffeeshop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CoffeeAdapter extends RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder> {

    private Context context;
    private List<CoffeeModel> coffeeList;
    private OnEditClickListener editClickListener;

    @FunctionalInterface
    public interface OnEditClickListener {
        void onEditClick(CoffeeModel coffee);
    }

    public CoffeeAdapter(Context context, List<CoffeeModel> coffeeList, OnEditClickListener editClickListener) {
        this.context = context;
        this.coffeeList = coffeeList;
        this.editClickListener = editClickListener;
    }

    @NonNull
    @Override
    public CoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.display_coffee_admin, parent, false);
        return new CoffeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoffeeViewHolder holder, int position) {
        CoffeeModel coffee = coffeeList.get(position);
        holder.name.setText(coffee.getName());
        holder.description.setText(coffee.getDescription());
        holder.price.setText("$" + coffee.getPrice());

        holder.edit.setOnClickListener(v -> editClickListener.onEditClick(coffee));

        holder.delete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("coffeeTable").document(coffee.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        coffeeList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, coffeeList.size());
                    });
        });

        if (coffee.getImageBase64() != null) {
            try {
                byte[] decodedString = Base64.decode(coffee.getImageBase64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.image.setImageBitmap(decodedByte);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return coffeeList.size();
    }

    public class CoffeeViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, price;
        Button edit, delete;
        ImageView image;

        public CoffeeViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewName);
            description = itemView.findViewById(R.id.textViewDescription);
            price = itemView.findViewById(R.id.textViewPrice);
            edit = itemView.findViewById(R.id.editButton);
            delete = itemView.findViewById(R.id.deleteButton);
            image = itemView.findViewById(R.id.imageViewCoffee);
        }
    }
}
