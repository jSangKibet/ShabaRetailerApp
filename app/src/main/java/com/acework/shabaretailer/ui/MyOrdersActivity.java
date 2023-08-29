package com.acework.shabaretailer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.StatusDialog;
import com.acework.shabaretailer.adapter.OrderAdapterNew;
import com.acework.shabaretailer.databinding.ActivityMyOrdersBinding;
import com.acework.shabaretailer.model.OrderNew;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {
    ActivityMyOrdersBinding binding;
    private OrderAdapterNew orderAdapter;

    private final ActivityResultLauncher<Intent> oiaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            loadOrders();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initializeList();
        loadOrders();
    }

    private void setListeners() {
        binding.backButton.setOnClickListener(v -> finish());
    }

    private void initializeList() {
        orderAdapter = new OrderAdapterNew(this, this::orderSelected);
        binding.orderList.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        StatusDialog statusDialog = StatusDialog.newInstance(R.raw.loading, "Fetching your orders...", false, null);
        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u != null) {
            FirebaseFirestore.getInstance().collection("orders").whereEqualTo("retailerId", u.getUid()).orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
                statusDialog.dismiss();
                if (task.isSuccessful()) {
                    List<OrderNew> retrievedOrders = new ArrayList<>();
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        retrievedOrders.add(qds.toObject(OrderNew.class));
                    }
                    orderAdapter.setItems(retrievedOrders);
                    if (retrievedOrders.size() < 1) binding.emptyList.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(binding.backButton, "There was a problem fetching your orders. Please try again later.", Snackbar.LENGTH_LONG).setAction("Retry", v -> loadOrders()).show();
                    if (task.getException() != null) task.getException().printStackTrace();
                }
            });
        }
    }

    private void orderSelected(OrderNew order) {
        Intent orderInfoIntent = new Intent(this, OrderInformationActivity.class);
        orderInfoIntent.putExtra("orderId", order.id);
        oiaLauncher.launch(orderInfoIntent);
    }
}