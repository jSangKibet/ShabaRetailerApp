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
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ItemInCartAdapter extends RecyclerView.Adapter<ItemInCartAdapter.ItemViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private final ItemActionListener itemActionListener;
    private List<Item> itemsInCart;

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
        int total=item.getPrice()*item.getQuantity();
        holder.name.setText(item.getName());
        holder.total.setText(context.getString(R.string.total_in_cart, item.getPrice(), item.getQuantity(), total));
        holder.edit.setOnClickListener(v -> itemActionListener.itemSelected(item));
        holder.delete.setOnClickListener(v -> itemActionListener.itemRemoved(item));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Item> itemsToDisplay) {
        itemsInCart=itemsToDisplay;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemsInCart.size();
    }


    public interface ItemActionListener {
        void itemSelected(Item item);
        void itemRemoved(Item item);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, total;
        private final MaterialButton edit, delete;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            edit = itemView.findViewById(R.id.edit);
            total = itemView.findViewById(R.id.total);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
