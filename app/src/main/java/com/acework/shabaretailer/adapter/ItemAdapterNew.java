package com.acework.shabaretailer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.atlas.ObjectHandler;
import com.acework.shabaretailer.model.ItemNew;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapterNew extends RecyclerView.Adapter<ItemAdapterNew.ItemViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private final ObjectHandler<ItemNew> viewItemHandler;
    private List<ItemNew> items;

    public ItemAdapterNew(Context context, ObjectHandler<ItemNew> viewItemHandler) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewItemHandler = viewItemHandler;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_item_new, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemNew item = items.get(position);
        holder.name.setText(item.name);
        holder.view.setOnClickListener(v -> viewItemHandler.handle(item));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<ItemNew> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView image;
        private final MaterialButton view;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            view = itemView.findViewById(R.id.view);
        }
    }
}
