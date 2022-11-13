package com.acework.shabaretailer.catalog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.CatalogActivity;
import com.acework.shabaretailer.R;
import com.acework.shabaretailer.adapter.ItemAdapter;
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class CatalogFragment extends Fragment {
    private TextInputEditText searchField;
    private ItemAdapter adapter;
    private ConstraintLayout summary;
    private TextView numOfItems, totalQuantity, total;
    private MaterialButton complete;
    private RecyclerView itemList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        initializeList();
        setSearchFunctionality();
        CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.getCart().observe(getViewLifecycleOwner(), itemsInCart -> {
            adapter.setItems(itemsInCart);
            computeTotals(itemsInCart);
        });
        complete.setOnClickListener(v -> toCart());
    }

    private void bindViews(View view) {
        itemList = view.findViewById(R.id.item_list);
        searchField = view.findViewById(R.id.search_field);
        summary = view.findViewById(R.id.summary_layout);
        numOfItems = view.findViewById(R.id.item_count);
        totalQuantity = view.findViewById(R.id.total_quantity);
        total = view.findViewById(R.id.total);
        complete = view.findViewById(R.id.complete_order);
    }

    private void initializeList() {
        itemList.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ItemAdapter(requireContext(), this::itemSelected);
        itemList.setAdapter(adapter);
    }

    private void itemSelected(Item item) {
        ((CatalogActivity) requireActivity()).toCartItem(item, true);
    }

    private void setSearchFunctionality() {
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s.toString());
            }
        });
    }

    private void computeTotals(List<Item> itemsInCart) {
        int uniqueItemsCount = 0;
        int allItemsCount = 0;
        int totalPrice = 0;

        for (Item itemInCart : itemsInCart) {
            if (itemInCart.getQuantity() > 0) {
                uniqueItemsCount++;
                allItemsCount += itemInCart.getQuantity();
                totalPrice += (itemInCart.getQuantity() * itemInCart.getPrice());
            }
        }

        if (uniqueItemsCount > 0) {
            numOfItems.setText(getString(R.string.unique_tem_count, uniqueItemsCount));
            totalQuantity.setText(getString(R.string.total_item_count, allItemsCount));
            total.setText(getString(R.string.order_total, totalPrice));
            summary.setVisibility(View.VISIBLE);
        } else {
            summary.setVisibility(View.GONE);
        }
    }

    private void toCart() {
        ((CatalogActivity) requireActivity()).toCart();
    }
}