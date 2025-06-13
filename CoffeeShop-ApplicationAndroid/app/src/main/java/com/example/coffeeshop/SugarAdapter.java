package com.example.coffeeshop;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;


public class SugarAdapter extends RecyclerView.Adapter<SugarAdapter.SugarViewHolder> {

    private List<SugarModel> sugarList;
    private Context context;
    private FirebaseFirestore db;
    private OnSugarEditListener editListener;

    public interface OnSugarEditListener {
        void onEdit(SugarModel item);
    }

    public SugarAdapter(Context context, List<SugarModel> sugarList, OnSugarEditListener editListener) {
        this.context = context;
        this.sugarList = sugarList;
        this.db = FirebaseFirestore.getInstance();
        this.editListener = editListener;
    }

    @Override
    public SugarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_sugar, parent, false);
        return new SugarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SugarViewHolder holder, int position) {
        SugarModel item = sugarList.get(position);
        holder.type.setText("Type: " + item.getType());
        holder.bags.setText("Bags: " + item.getBagCount());

        holder.deleteButton.setOnClickListener(v -> deleteSugar(item, position));
        holder.editButton.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEdit(item); // Trigger the activity’s method
            }
        });
    }

    @Override
    public int getItemCount() {
        return sugarList.size();
    }

    private void deleteSugar(SugarModel item, int position) {
        db.collection("sugarOptions")
                .document(item.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    sugarList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Sugar Deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error deleting sugar", Toast.LENGTH_SHORT).show();
                });
    }

    public static class SugarViewHolder extends RecyclerView.ViewHolder {
        TextView type, bags;
        Button deleteButton, editButton;

        public SugarViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.textSugarType);
            bags = itemView.findViewById(R.id.textSugarBags);
            deleteButton = itemView.findViewById(R.id.buttonDeleteSugar);
            editButton = itemView.findViewById(R.id.buttonEditSugar);
        }
    }
}
