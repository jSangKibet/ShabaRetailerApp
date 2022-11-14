package com.acework.shabaretailer.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.acework.shabaretailer.CatalogActivity;
import com.acework.shabaretailer.R;
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.model.Order;
import com.acework.shabaretailer.model.Retailer;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class ConfirmOrderFragment extends Fragment {
    private TextView itemTotal, estWeight, estTrans, estTotal, name, county, street, telephone, email;
    private CheckBox tc;
    private MaterialButton confirm, back;
    private CartViewModel cartViewModel;
    private String uid;


    public ConfirmOrderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirm_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setListeners();
        setValues();
    }

    private void bindViews(View view) {
        itemTotal = view.findViewById(R.id.item_total);
        estWeight = view.findViewById(R.id.est_weight);
        estTrans = view.findViewById(R.id.est_transport);
        estTotal = view.findViewById(R.id.est_total);
        name = view.findViewById(R.id.name);
        county = view.findViewById(R.id.county);
        street = view.findViewById(R.id.street);
        telephone = view.findViewById(R.id.telephone);
        email = view.findViewById(R.id.email);
        tc = view.findViewById(R.id.tc_checkbox);
        confirm = view.findViewById(R.id.confirm);
        back = view.findViewById(R.id.back_button);
    }

    @SuppressWarnings("ConstantConditions")
    private void setValues() {
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.getCart().observe(getViewLifecycleOwner(), this::computeValues);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference shabaRealtimeDbRef = FirebaseDatabase.getInstance().getReference().child("Retailers/" + uid);
        shabaRealtimeDbRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Retailer retailer = task.getResult().getValue(Retailer.class);
                setUserInfo(retailer);
            } else {
                Snackbar.make(requireView(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                confirm.setEnabled(false);
                task.getException().printStackTrace();
            }
        });
    }

    private void computeValues(List<Item> itemsInCart) {
        int totalPrice = 0;
        double weightDbl = 0D;

        for (Item itemInCart : itemsInCart) {
            if (itemInCart.getQuantity() > 0) {
                totalPrice += (itemInCart.getQuantity() * itemInCart.getPrice());
                weightDbl += (itemInCart.getWeight() * itemInCart.getQuantity());
            }
        }

        itemTotal.setText(getString(R.string.kes, totalPrice));
        estWeight.setText(getString(R.string.weight_formatted, weightDbl));
        estTrans.setText(getString(R.string.kes, 0));
        estTotal.setText(getString(R.string.kes, totalPrice));

        Retailer currentRetailer = ((CatalogActivity) requireActivity()).getRetailer();
        if (currentRetailer != null) {
            int transPerKg = 500;
            if (currentRetailer.getCounty().equals("Nairobi")) {
                transPerKg = 250;
            }
            int finalTransCost = (int) Math.round(transPerKg * weightDbl);
            estTrans.setText(getString(R.string.kes, finalTransCost));
            estTotal.setText(getString(R.string.kes, totalPrice + finalTransCost));
        }
    }

    private void setUserInfo(Retailer retailer) {
        name.setText(retailer.getName());
        county.setText(retailer.getCounty());
        street.setText(retailer.getStreet());
        telephone.setText(retailer.getTelephone());
        email.setText(retailer.getEmail());
    }

    private void setListeners() {
        back.setOnClickListener(v -> back());
        confirm.setOnClickListener(v -> confirmOrder());
    }

    private void back() {
        ((CatalogActivity) requireActivity()).toCart();
    }

    private Order getOrder() {
        int totalPrice = 0;
        double weightDbl = 0D;
        int finalTransCost=0;
        List<Item> itemsInCart = getItemsInCart();

        for (Item itemInCart : itemsInCart) {
            if (itemInCart.getQuantity() > 0) {
                totalPrice += (itemInCart.getQuantity() * itemInCart.getPrice());
                weightDbl += (itemInCart.getWeight() * itemInCart.getQuantity());
            }
        }

        Retailer currentRetailer = ((CatalogActivity) requireActivity()).getRetailer();
        if (currentRetailer != null) {
            int transPerKg = 500;
            if (currentRetailer.getCounty().equals("Nairobi")) {
                transPerKg = 250;
            }
            finalTransCost = (int) Math.round(transPerKg * weightDbl);
        }

        long timestamp = System.currentTimeMillis();

        return new Order(
                uid + timestamp,
                uid,
                timestamp,
                "Pending",
                itemsInCart,
                totalPrice+finalTransCost,
                finalTransCost,
                0);
    }

    private List<Item> getItemsInCart() {
        List<Item> itemsInCart = new ArrayList<>();
        for (Item item : cartViewModel.getCart().getValue()) {
            if (item.getQuantity() > 0) itemsInCart.add(item);
        }
        return itemsInCart;
    }

    private void confirmOrder() {
        if(tc.isChecked()){
            Order order = getOrder();
            DatabaseReference shabaRealtimeDbRef = FirebaseDatabase.getInstance().getReference().child("RetailOrders");
            shabaRealtimeDbRef.child(order.getId()).setValue(order).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ((CatalogActivity) requireActivity()).orderCompleted();
                } else {
                    Snackbar.make(requireView(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    task.getException().printStackTrace();
                }
            });
        }else {
            Snackbar.make(requireView(), "You must accept the terms and conditions before confirmind an order", Snackbar.LENGTH_LONG).show();
        }
    }
}