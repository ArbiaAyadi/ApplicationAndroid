package com.example.coffeeshop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CoffeeModel> cartItems;
    private OnCartChangedListener cartChangedListener;

    public interface OnCartChangedListener {
        void onCartChanged();
        void onSugarSelectionRequested(int position, CoffeeModel item);
        void onCupSelectionRequested(int position, CoffeeModel item);


    }
    public CartAdapter(Context context, List<CoffeeModel> cartItems, OnCartChangedListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartChangedListener = listener;

    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.display_cart_coffee, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        CoffeeModel coffee = cartItems.get(position);

        holder.itemName.setText(coffee.getName());
        holder.itemPrice.setText(String.format("$%.2f", coffee.getPrice()));

        if (coffee.getSugar() != null) {
            holder.itemSugar.setText("Sugar: " + coffee.getSugar().getType() + " (" + coffee.getSugar().getBagCount() + " bags)");
            holder.selectSugarButton.setText("Modify Sugar");
            holder.selectSugarButton.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.itemSugar.setText("Sugar: None");
            holder.selectSugarButton.setText("Select Sugar");
        }

        if (coffee.getCup() != null) {
            holder.itemCup.setText("Cup: " + coffee.getCup().getSize() + " (" + coffee.getCup().getName() + ")");
            holder.selectCupButton.setText("Modify Cup");
            holder.selectCupButton.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.itemCup.setText("Cup: None");
            holder.selectCupButton.setText("Select Cup");
        }

        String base64Image = coffee.getImageBase64();
        if (base64Image != null && !base64Image.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(base64Image);
            holder.imageView.setImageBitmap(bitmap);
        }

        holder.selectSugarButton.setOnClickListener(v -> {
            if (cartChangedListener != null) {
                cartChangedListener.onSugarSelectionRequested(position, coffee);
            }
        });

        holder.selectCupButton.setOnClickListener(v -> {
            if (cartChangedListener != null) {
                cartChangedListener.onCupSelectionRequested(position, coffee);
            }
        });

        holder.removeButton.setOnClickListener(v -> {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());

            if (cartChangedListener != null) {
                cartChangedListener.onCartChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }




    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, itemPrice, itemSugar, itemCup;
        ImageView imageView;
        Button removeButton, selectSugarButton, selectCupButton;

        public CartViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textViewItemName);
            itemPrice = itemView.findViewById(R.id.textViewItemPrice);
            itemSugar = itemView.findViewById(R.id.textViewSugar);
            itemCup = itemView.findViewById(R.id.textViewCup);
            imageView = itemView.findViewById(R.id.imageViewCoffee);
            removeButton = itemView.findViewById(R.id.buttonRemove);
            selectSugarButton = itemView.findViewById(R.id.buttonSelectSugar);
            selectCupButton = itemView.findViewById(R.id.buttonSelectCup);
        }
    }
}
