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

import java.util.ArrayList;
import java.util.List;

public class ItemInOrderAdapter extends RecyclerView.Adapter<ItemInOrderAdapter.ItemViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private List<Item> itemsInCart;
    private int orderType;

    public ItemInOrderAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        itemsInCart = new ArrayList<>();
        orderType = 0;
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
        int priceToUse = item.getPriceWholesale();
        if (orderType == 1) priceToUse = item.getPriceConsignment();
        if (orderType == 2) priceToUse = item.getPriceShaba();

        int total = priceToUse * item.getQuantity();
        holder.name.setText(item.getName());
        holder.total.setText(context.getString(R.string.total_in_cart, priceToUse, item.getQuantity(), total));
        if (item.getInsertColour().equals("Dark brown")) {
            holder.insertColour.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dark_brown_circle));
        } else if (item.getInsertColour().equals("Maroon")) {
            holder.insertColour.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.maroon_circle));
        } else {
            holder.insertColour.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mustard_circle));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Item> itemsToDisplay, int orderType) {
        itemsInCart = itemsToDisplay;
        this.orderType = orderType;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemsInCart.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, total;
        private final ImageView insertColour;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            total = itemView.findViewById(R.id.total);
            insertColour = itemView.findViewById(R.id.insert_color);
        }
    }
}
