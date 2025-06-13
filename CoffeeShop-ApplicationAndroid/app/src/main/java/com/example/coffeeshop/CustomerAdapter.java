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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CoffeeViewHolder> {

    private Context context;
    private List<CoffeeModel> coffeeList;
    private List<CoffeeModel> cartItems;

    public CustomerAdapter(Context context, List<CoffeeModel> coffeeList, List<CoffeeModel> cartItems) {
        this.context = context;
        this.coffeeList = coffeeList;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.display_coffee_customer, parent, false);
        return new CoffeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoffeeViewHolder holder, int position) {
        CoffeeModel coffeeItem = coffeeList.get(position);
        holder.textViewName.setText(coffeeItem.getName());
        holder.textViewDescription.setText(coffeeItem.getDescription());
        holder.textViewPrice.setText("$" + coffeeItem.getPrice());

        if (coffeeItem.getImageBase64() != null && !coffeeItem.getImageBase64().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(coffeeItem.getImageBase64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Glide.with(context).load(decodedByte).placeholder(R.drawable.image_placeholder).into(holder.imageViewCoffee);
            } catch (Exception e) {
                holder.imageViewCoffee.setImageResource(R.drawable.image_placeholder);
            }
        } else {
            holder.imageViewCoffee.setImageResource(R.drawable.image_placeholder);
        }


        boolean isInCart = isItemInCart(coffeeItem);
        if (isInCart) {
            holder.buttonAddToCart.setText("✔ Added");
            holder.buttonAddToCart.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_green_dark));
        }

        holder.buttonAddToCart.setOnClickListener(v -> {
            if (!isItemInCart(coffeeItem)) {
                cartItems.add(coffeeItem);
                notifyItemChanged(holder.getAdapterPosition());
                Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isItemInCart(CoffeeModel item) {
        for (CoffeeModel cartItem : cartItems) {
            if (cartItem instanceof CoffeeModel && item.getName().equals(((CoffeeModel) cartItem).getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return coffeeList.size();
    }

    public void updateData(List<CoffeeModel> newCoffeeList, List<CoffeeModel> newCartItems) {
        this.coffeeList = newCoffeeList;
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    public static class CoffeeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCoffee;
        TextView textViewName, textViewDescription, textViewPrice;
        Button buttonAddToCart;

        public CoffeeViewHolder(View itemView) {
            super(itemView);
            imageViewCoffee = itemView.findViewById(R.id.imageViewCoffee);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
        }
    }
}
