package com.acework.shabaretailer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.atlas.ObjectHandler;
import com.acework.shabaretailer.custom.AutoScrollThumbView;
import com.acework.shabaretailer.model.ItemInCart;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private final ObjectHandler<String> itemActionListener;
    private List<ItemInCart> items;
    private int orderType = -1;

    public ItemAdapter(Context context, ObjectHandler<String> itemActionListener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.itemActionListener = itemActionListener;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemInCart item = items.get(position);
        holder.name.setText(item.getItem().getName());
        holder.quantity.setText(context.getString(R.string.qty, item.getItem().getQuantity()));
        holder.add.setOnClickListener(v -> itemActionListener.handle(item.getItem().getSku()));
        holder.edit.setOnClickListener(v -> itemActionListener.handle(item.getItem().getSku()));
        holder.image.loadImages(item.getItem().getSku(), position);

        if (item.getQuantity() > 0) {
            holder.add.setVisibility(View.GONE);
            holder.editLayout.setVisibility(View.VISIBLE);
        } else {
            holder.editLayout.setVisibility(View.GONE);
            holder.add.setVisibility(View.VISIBLE);
        }

        switch (orderType) {
            case 2:
                holder.price.setText(context.getString(R.string.price, item.getItem().getPriceShaba()));
                holder.add.setEnabled(true);
                break;
            case 1:
                holder.price.setText(context.getString(R.string.price, item.getItem().getPriceConsignment()));
                holder.add.setEnabled(true);
                break;
            case 0:
                holder.price.setText(context.getString(R.string.price, item.getItem().getPriceWholesale()));
                holder.add.setEnabled(true);
                break;
            default:
                holder.price.setText(R.string.order_type_not_set);
                holder.add.setEnabled(false);

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<ItemInCart> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOrderType(int orderType) {
        this.orderType = orderType;
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, price, quantity;
        private final AutoScrollThumbView image;
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
