package com.acework.shabaretailer.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.StatusDialog;
import com.acework.shabaretailer.databinding.ActivityConfirmOrderBinding;
import com.acework.shabaretailer.dialog.CompleteOrderMoreDialog;
import com.acework.shabaretailer.model.OrderItem;
import com.acework.shabaretailer.model.OrderNew;
import com.acework.shabaretailer.model.RetailerNew;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ConfirmOrderActivity extends AppCompatActivity {
    ActivityConfirmOrderBinding binding;
    private String orderType;
    private List<OrderItem> orderItems;
    private RetailerNew retailer;
    private String retailerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(v -> finish());
        binding.more.setOnClickListener(v -> showMoreInfo());
        binding.confirm.setOnClickListener(v -> placeOrder());

        unpackData();

        loadRetailer();
    }

    private void unpackData() {
        orderType = getIntent().getStringExtra("orderType");
        String orderItemsString = getIntent().getStringExtra("orderItems");

        Type listType = new TypeToken<List<OrderItem>>() {
        }.getType();

        orderItems = new Gson().fromJson(orderItemsString, listType);
    }

    private void loadRetailer() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            retailerId = user.getUid();
            FirebaseFirestore.getInstance().collection("retailers").document(retailerId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        retailer = documentSnapshot.toObject(RetailerNew.class);
                        if (retailer == null) {
                            Log.e("Firebase error", "Null");
                            Snackbar.make(binding.back, "There was an error getting your information. Check your connection and try again, or contact support.", Snackbar.LENGTH_LONG).show();
                        } else {
                            displayRetailerInfo();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase error", e.getMessage() == null ? "Could not get user" : e.getMessage());
                        Snackbar.make(binding.back, "There was an error getting your information. Check your connection and try again, or contact support.", Snackbar.LENGTH_LONG).show();
                    });
        } else {
            Log.e("Firebase error", "Null");
            Snackbar.make(binding.back, "There was an error getting your information. Check your connection and try again, or contact support.", Snackbar.LENGTH_LONG).show();
        }
    }

    private void displayRetailerInfo() {
        binding.name.setText(retailer.name);
        binding.county.setText(retailer.county);
        binding.street.setText(retailer.town);
        binding.email.setText(retailer.email);
        if (!retailer.lookbookOrdered) {
            binding.lbCheckbox.setVisibility(View.VISIBLE);
        }

        displayOrderInfo();
    }

    private void displayOrderInfo() {
        binding.orderType.setText(orderType);
        int bagTotal = 0;
        for (OrderItem item : orderItems) {
            bagTotal += (item.price * item.quantity);
        }
        binding.bagTotal.setText(getString(R.string.ksh_ph, bagTotal));

        int transport = retailer.county.equals("Nairobi") ? 250 : 500;
        binding.transport.setText(getString(R.string.ksh_ph, transport));
        binding.total.setText(getString(R.string.ksh_ph, (bagTotal + transport)));

        binding.tcCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> binding.confirm.setEnabled(isChecked));
    }

    private void showMoreInfo() {
        CompleteOrderMoreDialog d = new CompleteOrderMoreDialog();
        d.show(getSupportFragmentManager(), CompleteOrderMoreDialog.TAG);
    }

    private void placeOrder() {
        StatusDialog dialog = StatusDialog.newInstance(R.raw.loading, "Placing your order...", false, null);
        dialog.show(getSupportFragmentManager(), StatusDialog.TAG);

        OrderNew order = getOrder();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // set order id
            DocumentReference newOrderRef = db.collection("orders").document();
            order.id = newOrderRef.getId();

            // perform updates
            transaction.update(db.collection("retailers").document(CartViewModel.UID), "lookbookOrdered", order.includeLookbook);
            transaction.set(newOrderRef, order);
            return null;
        }).addOnSuccessListener(unused -> {
            if (dialog.isVisible()) dialog.dismissAllowingStateLoss();
            StatusDialog successDialog = StatusDialog.newInstance(R.raw.success, "Order placed!", true, this::orderPlaced);
            successDialog.show(getSupportFragmentManager(), StatusDialog.TAG);
        }).addOnFailureListener(e -> {
            if (dialog.isVisible()) dialog.dismissAllowingStateLoss();
            Log.e("Firebase error", e.getMessage() == null ? "Could not get user" : e.getMessage());
            Snackbar.make(binding.back, "Your order could not be placed at the moment. Please try again later or contact support.", Snackbar.LENGTH_LONG).show();
        });

    }

    private OrderNew getOrder() {
        OrderNew order = new OrderNew();
        order.orderType = orderType;
        order.retailerId = retailerId;
        order.timestamp = System.currentTimeMillis();
        order.orderItems = orderItems;
        order.includeLookbook = binding.lbCheckbox.isChecked();
        order.county = retailer.county;
        order.town = retailer.town;
        order.status = "Pending";
        return order;
    }

    private void orderPlaced() {
        setResult(RESULT_OK);
        finish();
    }
}