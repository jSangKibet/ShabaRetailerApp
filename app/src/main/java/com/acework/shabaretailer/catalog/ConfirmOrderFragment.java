package com.acework.shabaretailer.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.acework.shabaretailer.CatalogActivity;
import com.acework.shabaretailer.R;
import com.acework.shabaretailer.StatusDialog;
import com.acework.shabaretailer.atlas.Atlas;
import com.acework.shabaretailer.databinding.FragmentConfirmOrderBinding;
import com.acework.shabaretailer.dialog.CompleteOrderMoreDialog;
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.model.Order;
import com.acework.shabaretailer.model.Retailer;
import com.acework.shabaretailer.viewmodel.CartViewModel2;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.List;

public class ConfirmOrderFragment extends Fragment {
    private FragmentConfirmOrderBinding binding;
    private CartViewModel2 cartViewModel;
    private String uid;

    public ConfirmOrderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConfirmOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
        setValues();
    }

    private void setValues() {
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel2.class);
        displayOrderInfo();
        displayRetailerInfo();
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        assert u != null;
        this.uid = u.getUid();
    }

    private void setListeners() {
        binding.backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.confirm.setOnClickListener(v -> confirmOrder());
        binding.more.setOnClickListener(v -> {
            CompleteOrderMoreDialog d = new CompleteOrderMoreDialog();
            d.show(getChildFragmentManager(), CompleteOrderMoreDialog.TAG);
        });
    }

    private Order getOrder() {
        int totalValueOfItems = calculateItemTotal();
        int totalWeightOfItems = calculateItemWeight();
        int estimatedTransportCost = calculateEstimatedTransportCost(totalWeightOfItems);
        List<Item> itemsInCart = Atlas.getItemsInCart(cartViewModel.getItemsInCart());
        long timestamp = System.currentTimeMillis();
        Retailer retailer = cartViewModel.getRetailer();
        return new Order(
                uid,
                retailer,
                timestamp,
                "Pending",
                itemsInCart,
                totalValueOfItems + estimatedTransportCost,
                estimatedTransportCost,
                0,
                0,
                retailer.getCounty(),
                retailer.getStreet(),
                cartViewModel.getOrderType(),
                binding.lbCheckbox.isChecked() ? 1 : 0);
    }

    private void confirmOrder() {
        if (binding.tcCheckbox.isChecked()) {
            StatusDialog processingDialog = StatusDialog.newInstance(R.raw.loading, "Placing your order...", false, null);
            processingDialog.show(getChildFragmentManager(), StatusDialog.TAG);
            Order order = getOrder();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                // set order id
                DocumentReference newOrderRef = db.collection("orders").document();
                order.setId(newOrderRef.getId());

                // check if lookbook is included in the order
                Retailer retailer = order.getRetailer();
                if (binding.lbCheckbox.isChecked()) {
                    retailer.setLookbook(1);
                    binding.lbCheckbox.setChecked(false);
                }

                // perform updates
                transaction.update(db.collection("retailers").document(uid), "lookbook", retailer.getLookbook());
                transaction.set(newOrderRef, order);
                return null;
            }).addOnCompleteListener(task -> {
                processingDialog.dismiss();
                if (task.isSuccessful()) {
                    StatusDialog successDialog = StatusDialog.newInstance(R.raw.success, "Order placed!", true, () -> ((CatalogActivity) requireActivity()).orderCompleted());
                    successDialog.show(getChildFragmentManager(), StatusDialog.TAG);
                } else {
                    Snackbar.make(requireView(), "Your order could not be placed at the moment. Please try again later.", Snackbar.LENGTH_LONG).show();
                    if (task.getException() != null) task.getException().printStackTrace();
                }
            });
        } else {
            Snackbar.make(requireView(), "You must accept the terms and conditions before confirming an order", Snackbar.LENGTH_LONG).show();
        }
    }

    public void uncheckTC() {
        binding.tcCheckbox.setChecked(false);
    }

    private void displayOrderInfo() {
        int totalValueOfItems = calculateItemTotal();
        int totalWeightOfItems = calculateItemWeight();
        int estimatedTransportCost = calculateEstimatedTransportCost(totalWeightOfItems);

        // order type
        binding.orderType.setText(Atlas.getOrderTypeAsString(cartViewModel.getOrderType()));
        // total value of items in order
        binding.estTotal.setText(getString(R.string.kes, totalValueOfItems));
        // total weight of items
        binding.estWeight.setText(getString(R.string.weight_formatted, totalWeightOfItems));
        // estimated transport cost
        binding.estTransport.setText(getString(R.string.kes, estimatedTransportCost));
        // estimated total cost
        binding.estTotal.setText(getString(R.string.kes, totalValueOfItems + estimatedTransportCost));
    }

    private int calculateItemTotal() {
        return Atlas.calculateItemTotal(cartViewModel.getOrderType(), cartViewModel.getItemsInCart());
    }

    private int calculateItemWeight() {
        return Atlas.calculateItemWeight(cartViewModel.getItemsInCart());
    }

    private int calculateEstimatedTransportCost(int weight) {
        return Atlas.calculateEstimatedTransportCost(cartViewModel.getRetailer().getCounty(), weight);
    }

    private void displayRetailerInfo() {
        Retailer retailer = cartViewModel.getRetailer();
        binding.name.setText(retailer.getName());
        binding.county.setText(retailer.getCounty());
        binding.street.setText(retailer.getStreet());
        binding.telephone.setText(retailer.getTelephone());
        binding.email.setText(retailer.getEmail());
        if (retailer.getLookbook() == 0) {
            binding.lbCheckbox.setVisibility(View.VISIBLE);
        }
    }
}