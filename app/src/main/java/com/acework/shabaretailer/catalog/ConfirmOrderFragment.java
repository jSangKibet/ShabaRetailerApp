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
import com.acework.shabaretailer.StatusDialog;
import com.acework.shabaretailer.model.Cart;
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
    private TextView itemTotal, estWeight, estTrans, estTotal, name, county, street, telephone, email, orderType;
    private CheckBox tc;
    private MaterialButton confirm, back;
    private CartViewModel cartViewModel;
    private String uid;

    public ConfirmOrderFragment() {
    }

    public static int getTransportCost(int weight, int costPerKG) {
        int kg = weight / 1000;
        int remainder = weight % 1000;
        if (remainder > 0) kg += 1;
        return kg * costPerKG;
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
        orderType = view.findViewById(R.id.order_type);
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

    private void computeValues(Cart cart) {
        int totalPrice = 0;
        int totalWeight = 0;
        int orderTypeInt = cart.getOrderType();

        for (Item itemInCart : cart.getItems()) {
            int priceToUse = itemInCart.getPriceWholesale();
            if (orderTypeInt == 1) priceToUse = itemInCart.getPriceConsignment();
            if (orderTypeInt == 2) priceToUse = itemInCart.getPriceShaba();

            if (itemInCart.getQuantity() > 0) {
                totalPrice += (itemInCart.getQuantity() * priceToUse);
                totalWeight += (itemInCart.getWeight() * itemInCart.getQuantity());
            }
        }

        itemTotal.setText(getString(R.string.kes, totalPrice));
        estWeight.setText(getString(R.string.weight_formatted, totalWeight));
        estTrans.setText(getString(R.string.kes, 0));
        estTotal.setText(getString(R.string.kes, totalPrice));

        if (orderTypeInt == 2) {
            this.orderType.setText(R.string.commission);
        } else if (orderTypeInt == 1) {
            this.orderType.setText(R.string.consignment);
        } else {
            this.orderType.setText(R.string.wholesale);
        }

        Retailer currentRetailer = ((CatalogActivity) requireActivity()).getRetailer();
        if (currentRetailer != null) {
            int transPerKg = 500;
            if (currentRetailer.getCounty().equals("Nairobi")) {
                transPerKg = 250;
            }
            int finalTransCost = getTransportCost(totalWeight, transPerKg);
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
        back.setOnClickListener(v -> requireActivity().onBackPressed());
        confirm.setOnClickListener(v -> confirmOrder());
    }

    private Order getOrder() {
        int totalPrice = 0;
        int totalWeight = 0;
        int estTransCost = 0;
        List<Item> itemsInCart = getItemsInCart();

        for (Item itemInCart : itemsInCart) {
            int priceToUse = itemInCart.getPriceWholesale();
            if (cartViewModel.getOrderType() == 1) priceToUse = itemInCart.getPriceConsignment();
            if (cartViewModel.getOrderType() == 2) priceToUse = itemInCart.getPriceShaba();

            if (itemInCart.getQuantity() > 0) {
                totalPrice += (itemInCart.getQuantity() * priceToUse);
                totalWeight += (itemInCart.getWeight() * itemInCart.getQuantity());
            }
        }

        Retailer currentRetailer = ((CatalogActivity) requireActivity()).getRetailer();
        if (currentRetailer != null) {
            int transPerKg = 500;
            if (currentRetailer.getCounty().equals("Nairobi")) {
                transPerKg = 250;
            }
            estTransCost = getTransportCost(totalWeight, transPerKg);
        }

        long timestamp = System.currentTimeMillis();

        return new Order(
                uid + timestamp,
                uid,
                timestamp,
                "Pending",
                itemsInCart,
                totalPrice + estTransCost,
                estTransCost,
                0,
                0,
                currentRetailer == null ? "" : currentRetailer.getCounty(),
                currentRetailer == null ? "" : currentRetailer.getStreet(),
                cartViewModel.getOrderTypeAsString());

    }

    private List<Item> getItemsInCart() {
        List<Item> itemsInCart = new ArrayList<>();
        for (Item item : cartViewModel.getItemsInCart()) {
            if (item.getQuantity() > 0) itemsInCart.add(item);
        }
        return itemsInCart;
    }

    private void confirmOrder() {
        if (tc.isChecked()) {
            StatusDialog statusDialog = StatusDialog.newInstance(R.raw.loading, "Placing your order...", false, null);
            statusDialog.show(getChildFragmentManager(), StatusDialog.TAG);
            Order order = getOrder();
            DatabaseReference shabaRealtimeDbRef = FirebaseDatabase.getInstance().getReference().child("OrdersV2");
            shabaRealtimeDbRef.child(order.getId()).setValue(order).addOnCompleteListener(task -> {
                statusDialog.dismiss();
                if (task.isSuccessful()) {
                    StatusDialog statusDialog2 = StatusDialog.newInstance(R.raw.success, "Order placed!", true, () -> ((CatalogActivity) requireActivity()).orderCompleted());
                    statusDialog2.show(getChildFragmentManager(), StatusDialog.TAG);
                } else {
                    Snackbar.make(requireView(), "Your order could not be placed at the moment. Please try again later.", Snackbar.LENGTH_LONG).show();
                    task.getException().printStackTrace();
                }
            });
        } else {
            Snackbar.make(requireView(), "You must accept the terms and conditions before confirming an order", Snackbar.LENGTH_LONG).show();
        }
    }

    public void uncheckTC() {
        tc.setChecked(false);
    }
}