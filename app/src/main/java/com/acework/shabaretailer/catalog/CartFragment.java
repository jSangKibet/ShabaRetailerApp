package com.acework.shabaretailer.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.CatalogActivity;
import com.acework.shabaretailer.R;
import com.acework.shabaretailer.adapter.ItemInCartAdapter;
import com.acework.shabaretailer.model.Cart;
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.model.Retailer;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.button.MaterialButton;

public class CartFragment extends Fragment implements ItemInCartAdapter.ItemActionListener {
    private ItemInCartAdapter adapter;
    private TextView type, total, weight, transport, estimatedTotal;
    private MaterialButton complete, back;
    private RecyclerView itemList;
    private CartViewModel cartViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        initializeList();
        loadData();
        setListeners();
    }

    private void bindViews(View view) {
        itemList = view.findViewById(R.id.item_list);
        total = view.findViewById(R.id.total);
        complete = view.findViewById(R.id.complete_order);
        weight = view.findViewById(R.id.weight);
        transport = view.findViewById(R.id.transport);
        estimatedTotal = view.findViewById(R.id.estimated_total);
        back = view.findViewById(R.id.back_button);
        type = view.findViewById(R.id.type);
    }

    private void initializeList() {
        itemList.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ItemInCartAdapter(requireContext(), this);
        itemList.setAdapter(adapter);
    }

    private void loadData() {
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.getCart().observe(getViewLifecycleOwner(), cart -> {
            adapter.setItems(cart);
            computeTotals(cart);
        });
    }

    private void setListeners() {
        back.setOnClickListener(v -> requireActivity().onBackPressed());
        complete.setOnClickListener(v -> toConfirmOrder());
    }

    private void computeTotals(Cart cart) {
        int totalPrice = 0;
        int totalWeight = 0;

        for (Item itemInCart : cart.getItems()) {
            int priceToUse = itemInCart.getPriceWholesale();
            if (cart.getOrderType() == 1) priceToUse = itemInCart.getPriceConsignment();
            if (cart.getOrderType() == 2) priceToUse = itemInCart.getPriceShaba();

            if (itemInCart.getQuantity() > 0) {
                totalPrice += (itemInCart.getQuantity() * priceToUse);
                totalWeight += (itemInCart.getWeight() * itemInCart.getQuantity());
            }
        }

        total.setText(getString(R.string.item_total, totalPrice));
        weight.setText(getString(R.string.weight_total, totalWeight));
        transport.setText(getString(R.string.transport, 0));
        estimatedTotal.setText(getString(R.string.total, totalPrice));
        type.setText(getString(R.string.order_type_ph, CartViewModel.getOrderTypeAsString(cart.getOrderType())));

        if (cart.getRetailer() != null) {
            int transPerKg = 500;
            if (cart.getRetailer().getCounty().equals("Nairobi")) {
                transPerKg = 250;
            }
            int finalTransCost = ConfirmOrderFragment.getTransportCost(totalWeight, transPerKg);
            transport.setText(getString(R.string.transport, finalTransCost));
            estimatedTotal.setText(getString(R.string.total, totalPrice + finalTransCost));
        }
    }

    @Override
    public void itemRemoved(Item item) {
        cartViewModel.removeItemFromCart(item);
    }

    @Override
    public void itemSelected(Item item) {
        ((CatalogActivity) requireActivity()).toCartItem(item);
    }

    private void toConfirmOrder() {
        ((CatalogActivity) requireActivity()).toConfirmOrder();
    }
}