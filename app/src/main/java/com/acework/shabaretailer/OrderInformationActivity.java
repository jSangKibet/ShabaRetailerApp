package com.acework.shabaretailer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.adapter.ItemInOrderAdapter;
import com.acework.shabaretailer.model.Order;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderInformationActivity extends AppCompatActivity {
    private static boolean modified = false;
    private TextView id, date, total, status, transport, deliveryPoint, type;
    private RecyclerView items;
    private ItemInOrderAdapter adapter;
    private String orderId;
    private MaterialButton back, cancel, received, download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);
        bindViews();
        setListeners();
        initializeList();
        loadOrder();
        if (modified) {
            setResult(RESULT_OK);
        }
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
        cancel = findViewById(R.id.cancel);
        received = findViewById(R.id.received);
        download = findViewById(R.id.download);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        cancel.setOnClickListener(v -> confirmCanceling());
        received.setOnClickListener(v -> confirmReceived());
        download.setOnClickListener(v -> confirmDownloadingNote());
    }

    private void initializeList() {
        items.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemInOrderAdapter(this);
        items.setAdapter(adapter);
    }

    private void loadOrder() {
        String oid = getIntent().getStringExtra("oid");
        if (oid == null) {
            finish();
        } else {
            StatusDialog statusDialog = StatusDialog.newInstance(R.raw.loading, "Fetching order information...", false, null);
            statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

            FirebaseFirestore.getInstance().collection("orders").document(oid).get().addOnCompleteListener(task -> {
                statusDialog.dismiss();
                if (task.isSuccessful()) {
                    Order o = task.getResult().toObject(Order.class);
                    if (o == null) {
                        Snackbar.make(back, "There was an error loading the order. Please try again later.", Snackbar.LENGTH_LONG).show();
                        Log.e("NPE", "Retrieved order " + oid + " is null");
                    } else {
                        displayOrder(o);
                    }
                } else {
                    Snackbar.make(back, "There was an error loading the order. Please try again later.", Snackbar.LENGTH_LONG).show();
                    if (task.getException() != null) task.getException().printStackTrace();
                }
            });
        }
    }

    private void displayOrder(Order order) {
        orderId = order.getId();
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

        if (order.getStatus().equals("Pending")) {
            cancel.setVisibility(View.VISIBLE);
        }
        if (order.getStatus().equals("Dispatched")) {
            received.setVisibility(View.VISIBLE);
        }
        if (order.getStatus().equals("Received")) {
            download.setVisibility(View.VISIBLE);
            if (modified) confirmDownloadingNote();
        }
        modified = false;
    }

    private void confirmCanceling() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cancel order")
                .setMessage("Are you sure you want to cancel this order? This action is irreversible.")
                .setPositiveButton("Yes", (dialogInterface, i) -> cancel())
                .setNegativeButton("No", null)
                .show();
    }

    private void confirmReceived() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Order received")
                .setMessage("Have the items in the order been delivered to you?")
                .setPositiveButton("Yes", (dialogInterface, i) -> received())
                .setNegativeButton("No", null)
                .show();
    }

    private void cancel() {
        StatusDialog cd = StatusDialog.newInstance(R.raw.loading, "Canceling order", false, null);
        cd.show(getSupportFragmentManager(), StatusDialog.TAG);

        FirebaseFirestore.getInstance().collection("orders").document(orderId).update("status", "Canceled").addOnCompleteListener(task -> {
            cd.dismiss();
            if (task.isSuccessful()) {
                StatusDialog sd = StatusDialog.newInstance(R.raw.success, "Order canceled", true, () -> {
                    modified = true;
                    recreate();
                });
                sd.show(getSupportFragmentManager(), StatusDialog.TAG);
            } else {
                Snackbar.make(back, "There was an error canceling the order. Please try again later.", Snackbar.LENGTH_LONG).show();
                if (task.getException() != null) task.getException().printStackTrace();
            }
        });
    }

    private void received() {
        StatusDialog cd = StatusDialog.newInstance(R.raw.loading, "Updating order", false, null);
        cd.show(getSupportFragmentManager(), StatusDialog.TAG);

        FirebaseFirestore.getInstance().collection("orders").document(orderId).update("status", "Received").addOnCompleteListener(task -> {
            cd.dismiss();
            if (task.isSuccessful()) {
                StatusDialog sd = StatusDialog.newInstance(R.raw.success, "Order completed", true, () -> {
                    modified = true;
                    recreate();
                });
                sd.show(getSupportFragmentManager(), StatusDialog.TAG);
            } else {
                Snackbar.make(back, "There was an error updating the order. Please try again later.", Snackbar.LENGTH_LONG).show();
                if (task.getException() != null) task.getException().printStackTrace();
            }
        });
    }

    private void confirmDownloadingNote() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Download delivery note")
                .setMessage("Would you like to download this order's delivery note as a PDF document? You can click the download icon at the top right to download later.")
                .setPositiveButton("Yes", (dialogInterface, i) -> downloadDocument())
                .setNegativeButton("No", null)
                .show();
    }

    private void downloadDocument() {
        Toast.makeText(this, "TODO: Download delivery note", Toast.LENGTH_SHORT).show();
    }
}