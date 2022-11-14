package com.acework.shabaretailer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemInOrderAdapter extends RecyclerView.Adapter<ItemInOrderAdapter.ItemViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private List<Item> itemsInCart;

    public ItemInOrderAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        itemsInCart = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_item_in_order, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemsInCart.get(position);
        int total = item.getPrice() * item.getQuantity();
        holder.name.setText(item.getName());
        holder.total.setText(context.getString(R.string.total_in_cart, item.getPrice(), item.getQuantity(), total));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Item> itemsToDisplay) {
        itemsInCart = itemsToDisplay;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemsInCart.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, total;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            total = itemView.findViewById(R.id.total);
        }
    }
}
