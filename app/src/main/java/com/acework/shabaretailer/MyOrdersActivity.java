package com.acework.shabaretailer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.adapter.OrderAdapter;
import com.acework.shabaretailer.model.Order;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {
    private MaterialButton back;
    private RecyclerView orderList;
    private OrderAdapter orderAdapter;
    private TextView emptyList;

    private final ActivityResultLauncher<Intent> oiaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            loadOrders();
        }
    });

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

    private void loadOrders() {
        StatusDialog statusDialog = StatusDialog.newInstance(R.raw.loading, "Fetching your orders...", false, null);
        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u != null) {
            FirebaseFirestore.getInstance().collection("orders").whereEqualTo("retailerId", u.getUid()).get().addOnCompleteListener(task -> {
                statusDialog.dismiss();
                if (task.isSuccessful()) {
                    List<Order> retrievedOrders = new ArrayList<>();
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        retrievedOrders.add(qds.toObject(Order.class));
                    }
                    orderAdapter.setItems(retrievedOrders);
                    if (retrievedOrders.size() < 1) emptyList.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(back, "There was a problem fetching your orders. Please try again later.", Snackbar.LENGTH_LONG).show();
                    if (task.getException() != null) task.getException().printStackTrace();
                }
            });
        }
    }

    private void orderSelected(Order order) {
        Intent orderInfoIntent = new Intent(this, OrderInformationActivity.class);
        orderInfoIntent.putExtra("oid", order.getId());
        oiaLauncher.launch(orderInfoIntent);
    }
}