package com.acework.shabaretailer.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import com.acework.shabaretailer.model.OrderNew;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapterNew extends RecyclerView.Adapter<OrderAdapterNew.ItemViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private final ObjectHandler<OrderNew> orderClicked;
    private List<OrderNew> myOrders;

    public OrderAdapterNew(Context context, ObjectHandler<OrderNew> orderClicked) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.orderClicked = orderClicked;
        myOrders = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_order, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        OrderNew order = myOrders.get(position);
        holder.number.setText(order.id);
        holder.total.setText(context.getString(R.string.ksh_ph, order.getTotal()));
        holder.type.setText(order.orderType);
        holder.status.setText(order.status);
        holder.container.setOnClickListener(v -> orderClicked.handle(order));
        holder.copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Order number", order.id);
            clipboard.setPrimaryClip(clip);
            Snackbar.make(holder.number, "Order number copied!", Snackbar.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<OrderNew> orders) {
        myOrders = orders;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView number, total, status, type;
        private final ConstraintLayout container;
        private final MaterialButton copy;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.order_number);
            total = itemView.findViewById(R.id.order_total);
            status = itemView.findViewById(R.id.order_status);
            container = itemView.findViewById(R.id.container);
            copy = itemView.findViewById(R.id.copy);
            type = itemView.findViewById(R.id.order_type);
        }
    }
}
