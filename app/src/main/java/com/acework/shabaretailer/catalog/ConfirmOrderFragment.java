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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfirmOrderFragment extends Fragment {
    private TextView itemTotal, estWeight, estTrans, estTotal, name, county, street, telephone, email, orderType;
    private CheckBox tc, lb;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        lb = view.findViewById(R.id.lb_checkbox);
    }

    private void setValues() {
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.getCart().observe(getViewLifecycleOwner(), this::computeValues);
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        assert u != null;
        this.uid = u.getUid();
    }

    private void computeValues(Cart cart) {
        setUserInfo(cart.getRetailer());
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

        int transPerKg = 500;
        if (cart.getRetailer() != null) {
            if (cart.getRetailer().getCounty().equals("Nairobi")) {
                transPerKg = 250;
            }
            int finalTransCost = getTransportCost(totalWeight, transPerKg);
            estTrans.setText(getString(R.string.kes, finalTransCost));
            estTotal.setText(getString(R.string.kes, totalPrice + finalTransCost));
        }
    }

    private void setUserInfo(Retailer retailer) {
        if (retailer == null) {
            confirm.setEnabled(false);
        } else {
            name.setText(retailer.getName());
            county.setText(retailer.getCounty());
            street.setText(retailer.getStreet());
            telephone.setText(retailer.getTelephone());
            email.setText(retailer.getEmail());
            confirm.setEnabled(true);
            if (retailer.getLookbook() == 0) {
                lb.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setListeners() {
        back.setOnClickListener(v -> requireActivity().onBackPressed());
        confirm.setOnClickListener(v -> confirmOrder());
    }

    private Order getOrder() {
        int totalPrice = 0;
        int totalWeight = 0;
        int estTransCost;
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

        Retailer retailer = Objects.requireNonNull(cartViewModel.getCart().getValue()).getRetailer();
        int transPerKg = 500;
        if (retailer.getCounty().equals("Nairobi")) {
            transPerKg = 250;
        }
        estTransCost = getTransportCost(totalWeight, transPerKg);

        long timestamp = System.currentTimeMillis();

        return new Order(uid, retailer, timestamp, "Pending", itemsInCart, totalPrice + estTransCost, estTransCost, 0, 0, retailer.getCounty(), retailer.getStreet(), cartViewModel.getOrderType(), lb.isChecked() ? 1 : 0);

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

            DocumentReference newOrderRef = FirebaseFirestore.getInstance().collection("orders").document();
            order.setId(newOrderRef.getId());
            newOrderRef.set(order).addOnCompleteListener(task -> {
                statusDialog.dismiss();
                if (task.isSuccessful()) {
                    StatusDialog statusDialog2 = StatusDialog.newInstance(R.raw.success, "Order placed!", true, () -> ((CatalogActivity) requireActivity()).orderCompleted());
                    statusDialog2.show(getChildFragmentManager(), StatusDialog.TAG);
                    setLookbookOrdered();
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
        tc.setChecked(false);
    }

    private void setLookbookOrdered() {
        if (lb.isChecked()) {
            lb.setChecked(false);
            Cart c = cartViewModel.getCart().getValue();
            if (c != null) {
                Retailer r = c.getRetailer();
                r.setLookbook(1);
                FirebaseFirestore.getInstance().collection("retailers").document(uid).set(r).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        if (task.getException() != null) task.getException().printStackTrace();
                    }
                });
            }
        }
    }
}