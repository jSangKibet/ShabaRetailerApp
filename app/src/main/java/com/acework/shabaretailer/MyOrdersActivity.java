package com.acework.shabaretailer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.adapter.OrderAdapter;
import com.acework.shabaretailer.model.Order;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {
    private MaterialButton back;
    private RecyclerView orderList;
    private OrderAdapter orderAdapter;
    private TextView emptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        bindViews();
        setListeners();
        initializeList();
        loadOrders();
    }

    private void bindViews() {
        back = findViewById(R.id.back_button);
        orderList = findViewById(R.id.order_list);
        emptyList = findViewById(R.id.empty_list);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
    }

    private void initializeList() {
        orderList.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, this::orderSelected);
        orderList.setAdapter(orderAdapter);
    }

    @SuppressWarnings("ConstantConditions")
    private void loadOrders() {
        StatusDialog statusDialog = StatusDialog.newInstance(R.raw.loading, "Fetching your orders...", false, null);
        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference shabaRtDbRef = FirebaseDatabase.getInstance().getReference().child("OrdersV3");
        shabaRtDbRef.orderByChild("retailerId").equalTo(uid).get().addOnCompleteListener(task -> {
            statusDialog.dismiss();
            if (task.isSuccessful()) {
                List<Order> retrievedOrders = new ArrayList<>();
                for (DataSnapshot child : task.getResult().getChildren()) {
                    retrievedOrders.add(child.getValue(Order.class));
                }
                orderAdapter.setItems(retrievedOrders);
                if (retrievedOrders.size() < 1) emptyList.setVisibility(View.VISIBLE);
            } else {
                task.getException().printStackTrace();
                Snackbar.make(back, "There was a problem fetching your orders. Please try again later.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void orderSelected(Order order) {
        Intent orderInfoIntent = new Intent(this, OrderInformationActivity.class);
        orderInfoIntent.putExtra("oid", order.getId());
        startActivity(orderInfoIntent);
    }
}