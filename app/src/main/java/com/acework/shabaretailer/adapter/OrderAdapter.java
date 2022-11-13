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
import com.acework.shabaretailer.model.Order;

import java.util.ArrayList;
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
            holder.total.setText(context.getString(R.string.order_total_est, order.getEstimatedTotal()));
        } else {
            holder.total.setText(context.getString(R.string.order_total, order.getFinalTotal()));
        }
        holder.status.setText(order.getStatus());
        holder.container.setOnClickListener(v -> orderActionListener.orderSelected(order));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Order> orders) {
        myOrders = orders;
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
        private final TextView number, total, status;
        private final ConstraintLayout container;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.order_number);
            total = itemView.findViewById(R.id.order_total);
            status = itemView.findViewById(R.id.order_status);
            container = itemView.findViewById(R.id.container);
        }
    }
}
