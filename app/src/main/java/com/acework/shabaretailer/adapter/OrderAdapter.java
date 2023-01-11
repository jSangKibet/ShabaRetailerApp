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
import com.acework.shabaretailer.model.Order;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ItemViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private final OrderActionListener orderActionListener;
    private List<Order> myOrders;

    public OrderAdapter(Context context, OrderActionListener orderActionListener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.orderActionListener = orderActionListener;
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
        Order order = myOrders.get(position);
        holder.number.setText(order.getId());
        if (order.getFinalTotal() == 0) {
            holder.total.setText(context.getString(R.string.kes_est, order.getEstimatedTotal()));
        } else {
            holder.total.setText(context.getString(R.string.kes, order.getFinalTotal()));
        }
        holder.type.setText(CartViewModel.getOrderTypeAsString(order.getType()));
        holder.status.setText(order.getStatus());
        holder.container.setOnClickListener(v -> orderActionListener.orderSelected(order));
        holder.copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Order number", order.getId());
            clipboard.setPrimaryClip(clip);
            Snackbar.make(holder.number, "Order number copied!", Snackbar.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Order> orders) {
        myOrders = orders;
        Collections.reverse(myOrders);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }

    public interface OrderActionListener {
        void orderSelected(Order order);
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
