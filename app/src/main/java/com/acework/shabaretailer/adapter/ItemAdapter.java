package com.acework.shabaretailer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.model.Item;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private final ItemActionListener itemActionListener;
    private final StorageReference shabaItemImagesCloudStorageRef;
    private List<Item> allItems;
    private List<Item> filteredItems;

    public ItemAdapter(Context context, ItemActionListener itemActionListener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.itemActionListener = itemActionListener;
        allItems = new ArrayList<>();
        filteredItems = new ArrayList<>();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        shabaItemImagesCloudStorageRef = firebaseStorage.getReference().child("item_images");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = filteredItems.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(context.getString(R.string.price, item.getPrice()));
        holder.quantity.setText(context.getString(R.string.qty, item.getQuantity()));
        holder.add.setOnClickListener(v -> itemActionListener.itemSelected(item));
        holder.edit.setOnClickListener(v -> itemActionListener.itemSelected(item));
        setImage(item.getSku(), holder.image);
        if (item.getQuantity() > 0) {
            holder.add.setVisibility(View.GONE);
            holder.editLayout.setVisibility(View.VISIBLE);
        } else {
            holder.editLayout.setVisibility(View.GONE);
            holder.add.setVisibility(View.VISIBLE);
        }
    }

    public void setItems(List<Item> itemsToDisplay) {
        allItems = itemsToDisplay;
        filter("");
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String key) {
        if (key.isEmpty()) {
            filteredItems = allItems;
        } else {
            filteredItems = new ArrayList<>();
            for (Item item : allItems) {
                if (item.getName().toLowerCase().contains(key.toLowerCase())) {
                    filteredItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    private void setImage(String itemSku, ImageView imageView) {
        String imageName = itemSku + "_01.jpg";
        shabaItemImagesCloudStorageRef.child(imageName).getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(imageView).load(task.getResult()).placeholder(R.drawable.image_96).into(imageView);
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.image_96));
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateQuantities(HashMap<String, Integer> itemQuantities) {
        for (Item item : allItems) {
            Integer itemQty = itemQuantities.get(item.getSku());
            item.setQuantity(itemQty == null ? 0 : itemQty);
        }
        notifyDataSetChanged();
    }

    public interface ItemActionListener {
        void itemSelected(Item item);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, price, quantity;
        private final ImageView image;
        private final MaterialButton add, edit;
        private final ConstraintLayout editLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
            add = itemView.findViewById(R.id.add);
            quantity = itemView.findViewById(R.id.quantity);
            edit = itemView.findViewById(R.id.edit);
            editLayout = itemView.findViewById(R.id.edit_layout);
        }
    }
}
