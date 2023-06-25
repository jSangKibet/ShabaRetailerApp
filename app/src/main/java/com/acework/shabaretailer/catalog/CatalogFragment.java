package com.acework.shabaretailer.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.acework.shabaretailer.CatalogActivity;
import com.acework.shabaretailer.R;
import com.acework.shabaretailer.adapter.ItemAdapter;
import com.acework.shabaretailer.atlas.Atlas;
import com.acework.shabaretailer.custom.GridSpacingItemDecoration;
import com.acework.shabaretailer.databinding.FragmentCatalogBinding;
import com.acework.shabaretailer.dialog.SetOrderTypeDialog;
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CatalogFragment extends Fragment {
    private FragmentCatalogBinding binding;
    private ItemAdapter adapter;
    private CartViewModel cartViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeList();
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        setListeners();
        fetchItems();
    }

    private void initializeList() {
        binding.itemList.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ItemAdapter(requireContext(), this::itemSelected);
        binding.itemList.setAdapter(adapter);
        binding.itemList.addItemDecoration(new GridSpacingItemDecoration(2, 8, true));
    }

    private void setListeners() {
        binding.completeOrder.setOnClickListener(v -> toCart());
        binding.menuButton.setOnClickListener(v -> ((CatalogActivity) requireActivity()).openDrawer());
        binding.setOrderType.setOnClickListener(v -> setOrderType());
    }

    private void itemSelected(String sku) {
        ((CatalogActivity) requireActivity()).toCartItem(sku);
    }

    private void toCart() {
        ((CatalogActivity) requireActivity()).toCart();
    }

    private void fetchItems() {
        FirebaseFirestore.getInstance().collection("items").get().addOnCompleteListener(task -> {
            hideAnimation();
            if (task.isSuccessful()) {
                List<Item> itemsFromDb = new ArrayList<>();
                for (QueryDocumentSnapshot qds : task.getResult()) {
                    Item i = qds.toObject(Item.class);
                    itemsFromDb.add(i);
                }
                cartViewModel.setItemsInCart(itemsFromDb);
                setQuantityObserver();
            } else {
                binding.errorMessage.setVisibility(View.VISIBLE);
                if (task.getException() != null) task.getException().printStackTrace();
            }
        });
    }

    private void setQuantityObserver() {
        cartViewModel.getItemsInCartLive().observe(getViewLifecycleOwner(), itemsInCart -> {
            int count = Atlas.getItemsInCart(itemsInCart).size();
            int total = Atlas.calculateItemTotal(cartViewModel.getOrderType(), itemsInCart);
            adapter.setItems(itemsInCart);
            displayTotals(count, total);
        });
        cartViewModel.getOrderTypeLive().observe(getViewLifecycleOwner(), orderType -> {
            adapter.setOrderType(orderType);
            binding.setOrderType.setText(Atlas.getOrderTypeAsString(orderType));
        });
    }

    private void displayTotals(int count, int total) {
        if (count > 0) {
            binding.totalQuantity.setText(getString(R.string.total_item_count, count));
            binding.total.setText(getString(R.string.order_total, total));
            binding.summaryLayout.setVisibility(View.VISIBLE);
        } else {
            binding.summaryLayout.setVisibility(View.GONE);
        }
    }

    private void hideAnimation() {
        binding.loadingAnim.pauseAnimation();
        binding.loadingAnim.setVisibility(View.GONE);
    }

    private void setOrderType() {
        SetOrderTypeDialog dialog = SetOrderTypeDialog.newInstance(
                cartViewModel.getOrderType(), object -> cartViewModel.setOrderType(object)
        );
        dialog.show(getChildFragmentManager(), SetOrderTypeDialog.TAG);
    }
}