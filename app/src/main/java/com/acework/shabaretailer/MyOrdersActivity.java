package com.acework.shabaretailer;

import android.os.Bundle;
import android.widget.Toast;

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
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference shabaRtDbRef = FirebaseDatabase.getInstance().getReference().child("RetailOrders");
        shabaRtDbRef.orderByChild("retailerId").equalTo(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Order> retrievedOrders = new ArrayList<>();
                for (DataSnapshot child : task.getResult().getChildren()) {
                    retrievedOrders.add(child.getValue(Order.class));
                }
                orderAdapter.setItems(retrievedOrders);
            } else {
                Snackbar.make(back, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void orderSelected(Order order) {
        Toast.makeText(this, "Order selected!", Toast.LENGTH_SHORT).show();
    }
}