package com.acework.shabaretailer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.atlas.ObjectHandler;
import com.acework.shabaretailer.model.InsertColorChoice;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class InsertColorChoicesAdapter extends RecyclerView.Adapter<InsertColorChoicesAdapter.InsertChoiceVH> {
    private final LayoutInflater inflater;
    private final Context context;
    private final ObjectHandler<InsertColorChoice> colorDeletionHandler;
    private List<InsertColorChoice> insertColorChoices;

    public InsertColorChoicesAdapter(Context context, ObjectHandler<InsertColorChoice> colorDeletionHandler) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.colorDeletionHandler = colorDeletionHandler;
        insertColorChoices = new ArrayList<>();
    }

    @NonNull
    @Override
    public InsertChoiceVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_insert_choice, parent, false);
        return new InsertChoiceVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InsertChoiceVH holder, int position) {
        InsertColorChoice choice = insertColorChoices.get(position);
        if (choice.wahura > 0) {
            holder.qty.setText(String.valueOf(choice.wahura));
            holder.bagName.setText(R.string.wahura);
        } else {
            holder.qty.setText(String.valueOf(choice.twende));
            holder.bagName.setText(R.string.twende);
        }

        switch (choice.color) {
            case "Mustard":
                holder.insertColor.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mustard_circle));
                break;
            case "Maroon":
                holder.insertColor.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.maroon_circle));
                break;
            case "Dark brown":
                holder.insertColor.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dark_brown_circle));
                break;
            case "Dusty pink":
                holder.insertColor.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dusty_pink_circle));
                break;
            default:
                holder.insertColor.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.taupe_circle));
        }

        holder.remove.setOnClickListener(v -> {
            insertColorChoices.remove(position);
            notifyItemRemoved(position);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setInsertColorChoices(List<InsertColorChoice> choices) {
        this.insertColorChoices = choices;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return insertColorChoices.size();
    }


    public static class InsertChoiceVH extends RecyclerView.ViewHolder {
        private final TextView qty, bagName;
        private final MaterialButton remove;
        private final ImageView insertColor;

        public InsertChoiceVH(@NonNull View itemView) {
            super(itemView);
            qty = itemView.findViewById(R.id.qty);
            bagName = itemView.findViewById(R.id.bag_name);
            remove = itemView.findViewById(R.id.remove);
            insertColor = itemView.findViewById(R.id.insert_color);
        }
    }
}
