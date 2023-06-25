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
import com.acework.shabaretailer.adapter.ItemInCartAdapter;
import com.acework.shabaretailer.atlas.Atlas;
import com.acework.shabaretailer.databinding.FragmentCartBinding;
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.snackbar.Snackbar;

public class CartFragment extends Fragment implements ItemInCartAdapter.ItemActionListener {
    private FragmentCartBinding binding;
    private ItemInCartAdapter adapter;
    private CartViewModel cartViewModel;
    private int retailerLoaded = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeList();
        loadData();
        setListeners();
    }

    private void initializeList() {
        adapter = new ItemInCartAdapter(requireContext(), this);
        binding.itemList.setAdapter(adapter);
    }

    private void loadData() {
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        // display items & totals
        cartViewModel.getItemsInCartLive().observe(getViewLifecycleOwner(), itemsInCart -> {
            adapter.setItems(cartViewModel.getOrderType(), Atlas.getItemsInCart(itemsInCart));
            computeTotals();
        });
        cartViewModel.getRetailerLive().observe(getViewLifecycleOwner(), retailer -> {
            if (retailer == null) {
                binding.completeOrder.setEnabled(false);
                retailerLoaded = 2;
            } else {
                retailerLoaded = 1;
                binding.completeOrder.setEnabled(true);
            }
        });
    }

    private void setListeners() {
        binding.backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.completeOrder.setOnClickListener(v -> toConfirmOrder());
    }

    private void computeTotals() {
        int totalValueOfItems = Atlas.calculateItemTotal(cartViewModel.getOrderType(), cartViewModel.getItemsInCart());
        int totalWeightOfItems = Atlas.calculateItemWeight(cartViewModel.getItemsInCart());
        int estimatedTransportCost = Atlas.calculateEstimatedTransportCost(
                cartViewModel.getRetailer() == null ?
                        "Nairobi" :
                        cartViewModel.getRetailer().getCounty(),
                totalWeightOfItems);

        binding.total.setText(getString(R.string.item_total, totalValueOfItems));
        binding.weight.setText(getString(R.string.weight_total, totalWeightOfItems));
        binding.transport.setText(getString(R.string.transport, estimatedTransportCost));
        binding.estimatedTotal.setText(getString(R.string.total, totalValueOfItems + estimatedTransportCost));
        binding.type.setText(getString(R.string.order_type_ph, Atlas.getOrderTypeAsString(cartViewModel.getOrderType())));
    }

    @Override
    public void itemRemoved(Item item) {
        cartViewModel.removeItemFromCart(item);
    }

    @Override
    public void itemSelected(String sku) {
        ((CatalogActivity) requireActivity()).toCartItem(sku);
    }

    private void toConfirmOrder() {
        ((CatalogActivity) requireActivity()).toConfirmOrder();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (retailerLoaded == 2) {
                Snackbar.make(
                                binding.backButton,
                                "Retailer was not loaded properly. Please restart the app, or log out and log in again.",
                                Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }
}