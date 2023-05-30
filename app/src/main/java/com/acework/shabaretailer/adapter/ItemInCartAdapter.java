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
import com.acework.shabaretailer.model.Item;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ItemInCartAdapter extends RecyclerView.Adapter<ItemInCartAdapter.ItemViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private final ItemActionListener itemActionListener;
    private List<Item> itemsInCart;
    private int orderType = 0;

    public ItemInCartAdapter(Context context, ItemActionListener itemActionListener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.itemActionListener = itemActionListener;
        itemsInCart = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_item_in_cart, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemsInCart.get(position);
        holder.name.setText(item.getName());
        holder.edit.setOnClickListener(v -> itemActionListener.itemSelected(item.getSku()));
        holder.delete.setOnClickListener(v -> itemActionListener.itemRemoved(item));
        if (item.getInsertColour().equals("Dark brown")) {
            holder.insertColor.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dark_brown_circle));
        } else if (item.getInsertColour().equals("Maroon")) {
            holder.insertColor.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.maroon_circle));
        } else {
            holder.insertColor.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mustard_circle));
        }

        int priceToUse = item.getPriceWholesale();
        if (orderType == 1) priceToUse = item.getPriceConsignment();
        if (orderType == 2) priceToUse = item.getPriceShaba();

        int total = priceToUse * item.getQuantity();
        holder.total.setText(context.getString(R.string.total_in_cart, priceToUse, item.getQuantity(), total));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(int orderType, List<Item> itemsInCart) {
        this.orderType = orderType;
        this.itemsInCart = itemsInCart;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemsInCart.size();
    }


    public interface ItemActionListener {
        void itemSelected(String sku);

        void itemRemoved(Item item);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, total;
        private final MaterialButton edit, delete;
        private final ImageView insertColor;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            edit = itemView.findViewById(R.id.edit);
            total = itemView.findViewById(R.id.total);
            delete = itemView.findViewById(R.id.delete);
            insertColor = itemView.findViewById(R.id.insert_color);
        }
    }
}
