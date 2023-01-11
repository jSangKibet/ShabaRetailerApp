package com.acework.shabaretailer;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.adapter.ItemInOrderAdapter;
import com.acework.shabaretailer.model.Order;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderInformationActivity extends AppCompatActivity {
    private MaterialButton back;
    private TextView id, date, total, status, transport, deliveryPoint, type;
    private RecyclerView items;
    private ItemInOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);
        bindViews();
        setListeners();
        initializeList();
        loadOrder();
    }

    private void bindViews() {
        back = findViewById(R.id.back);
        id = findViewById(R.id.order_num);
        date = findViewById(R.id.date);
        total = findViewById(R.id.total);
        status = findViewById(R.id.order_status);
        items = findViewById(R.id.item_list);
        transport = findViewById(R.id.transport);
        deliveryPoint = findViewById(R.id.delivery_point);
        type = findViewById(R.id.type);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
    }

    private void initializeList() {
        items.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemInOrderAdapter(this);
        items.setAdapter(adapter);
    }

    @SuppressWarnings("ConstantConditions")
    private void loadOrder() {
        String oid = getIntent().getStringExtra("oid");
        if (oid == null) {
            finish();
        } else {
            StatusDialog statusDialog = StatusDialog.newInstance(R.raw.loading, "Fetching order information...", false, null);
            statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);
            DatabaseReference shabaRtDbRef = FirebaseDatabase.getInstance().getReference().child("OrdersV2").child(oid);
            shabaRtDbRef.get().addOnCompleteListener(task -> {
                statusDialog.dismiss();
                if (task.isSuccessful()) {
                    displayOrder(task.getResult().getValue(Order.class));
                } else {
                    Snackbar.make(back, "There was an error loading the order. Please try again later.", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private void displayOrder(Order order) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String formattedDate = dateFormatter.format(new Date(order.getTimestamp()));

        id.setText(getString(R.string.order_num, order.getId()));
        type.setText(CartViewModel.getOrderTypeAsString(order.getType()));
        date.setText(formattedDate);
        status.setText(order.getStatus());

        if (order.getFinalTotal() > 0) {
            total.setText(getString(R.string.kes, order.getFinalTotal()));
        } else {
            total.setText(getString(R.string.est_total, order.getEstimatedTotal()));
        }

        if (order.getFinalTransportCost() > 0) {
            transport.setText(getString(R.string.kes, order.getFinalTransportCost()));
        } else {
            transport.setText(getString(R.string.est_total, order.getEstimatedTransportCost()));
        }

        deliveryPoint.setText(getString(R.string.delivery_point, order.getCounty(), order.getStreet()));

        adapter.setItems(order.getOrderItems(), order.getType());
    }
}