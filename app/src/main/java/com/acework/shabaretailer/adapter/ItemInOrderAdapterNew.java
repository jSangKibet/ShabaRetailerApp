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
import com.acework.shabaretailer.atlas.Atlas;
import com.acework.shabaretailer.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class ItemInOrderAdapterNew extends RecyclerView.Adapter<ItemInOrderAdapterNew.ItemViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private List<OrderItem> orderItems;

    public ItemInOrderAdapterNew(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        orderItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_item_in_order, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        holder.name.setText(Atlas.getItemName(item.sku));
        holder.total.setText(context.getString(R.string.total_in_cart, item.price, item.quantity, (item.price * item.quantity)));
        switch (item.insertColor) {
            case "Dark brown":
                holder.insertColour.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dark_brown_circle));
                break;
            case "Mustard":
                holder.insertColour.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mustard_circle));
                break;
            case "Dusty pink":
                holder.insertColour.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dusty_pink_circle));
                break;
            default:
                holder.insertColour.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.taupe_circle));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<OrderItem> itemsToDisplay) {
        orderItems = itemsToDisplay;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
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
