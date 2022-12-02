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
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.model.Retailer;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements ItemInCartAdapter.ItemActionListener {
    private ItemInCartAdapter adapter;
    private TextView total, weight, transport, estimatedTotal;
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
    }

    private void initializeList() {
        itemList.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ItemInCartAdapter(requireContext(), this);
        itemList.setAdapter(adapter);
    }

    private void loadData() {
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.getCart().observe(getViewLifecycleOwner(), itemsInCart -> {
            adapter.setItems(getItemsInCart(itemsInCart));
            computeTotals(itemsInCart);
        });
    }

    private void setListeners() {
        back.setOnClickListener(v -> requireActivity().onBackPressed());
        complete.setOnClickListener(v -> toConfirmOrder());
    }

    private void computeTotals(List<Item> itemsInCart) {
        int totalPrice = 0;
        int totalWeight = 0;

        for (Item itemInCart : itemsInCart) {
            if (itemInCart.getQuantity() > 0) {
                totalPrice += (itemInCart.getQuantity() * itemInCart.getPrice());
                totalWeight += (itemInCart.getWeight() * itemInCart.getQuantity());
            }
        }

        total.setText(getString(R.string.item_total, totalPrice));
        weight.setText(getString(R.string.weight_total, totalWeight));
        transport.setText(getString(R.string.transport, 0));
        estimatedTotal.setText(getString(R.string.total, totalPrice));

        Retailer currentRetailer = ((CatalogActivity) requireActivity()).getRetailer();
        if (currentRetailer != null) {
            int transPerKg = 500;
            if (currentRetailer.getCounty().equals("Nairobi")) {
                transPerKg = 250;
            }
            int finalTransCost = ConfirmOrderFragment.getTransportCost(totalWeight, transPerKg);
            transport.setText(getString(R.string.transport, finalTransCost));
            estimatedTotal.setText(getString(R.string.total, totalPrice + finalTransCost));
        }
    }

    private List<Item> getItemsInCart(List<Item> cart) {
        List<Item> itemsInCart = new ArrayList<>();
        for (Item itemInCart : cart) {
            if (itemInCart.getQuantity() > 0) {
                itemsInCart.add(itemInCart);
            }
        }
        return itemsInCart;
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