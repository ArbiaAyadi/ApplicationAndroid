package com.example.coffeeshop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class CupSelectAdapter extends RecyclerView.Adapter<CupSelectAdapter.CupViewHolder> {

    private List<CupsModel> cupsList;
    private OnCupSelectedListener listener;

    private Context context;
    private int selectedPosition = -1;
    private CupsModel selectedCup = null;


    public interface OnCupSelectedListener {
        void onCupSelected(CupsModel selectedCup);
    }

    public CupSelectAdapter(Context context, List<CupsModel> cupsList, OnCupSelectedListener listener) {
        this.context = context;
        this.cupsList = cupsList;
        this.listener = listener;
    }

    @Override
    public CupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cup, parent, false);
        return new CupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CupViewHolder holder, int position) {
        CupsModel cup = cupsList.get(holder.getAdapterPosition());

        holder.textViewCupName.setText(cup.getName());
        holder.textViewCupSize.setText(cup.getSize());

        String base64Image = cup.getImageBase64();
        if (base64Image != null && !base64Image.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(base64Image);
            holder.imageViewCup.setImageBitmap(bitmap);
        } else {
            holder.imageViewCup.setImageResource(R.drawable.image_placeholder);
        }

        if (selectedCup != null && selectedCup.equals(cup)) {
            holder.itemView.setBackgroundResource(R.drawable.selected_item_border);
        } else {
            holder.itemView.setBackgroundResource(android.R.color.transparent);
        }


        holder.imageViewCup.setOnClickListener(v -> {
            selectedCup = cup;
            notifyDataSetChanged();
            if (listener != null) {
                listener.onCupSelected(cup);
            }
        });
    }


    public void updateCups(List<CupsModel> cups) {
        this.cupsList.clear();
        this.cupsList.addAll(cups);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cupsList.size();
    }

    public static class CupViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCupName, textViewCupSize;
        ImageView imageViewCup;

        public CupViewHolder(View itemView) {
            super(itemView);
            textViewCupName = itemView.findViewById(R.id.textViewCupName);
            textViewCupSize = itemView.findViewById(R.id.textViewCupSize);
            imageViewCup = itemView.findViewById(R.id.imageViewCup);
        }
    }

    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
