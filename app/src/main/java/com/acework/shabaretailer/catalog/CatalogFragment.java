package com.acework.shabaretailer.catalog;

import android.os.Bundle;
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
import com.acework.shabaretailer.custom.GridSpacingItemDecoration;
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CatalogFragment extends Fragment {
    private ItemAdapter adapter;
    private ConstraintLayout summary;
    private TextView totalQuantity, total, errorMessage;
    private MaterialButton complete, menuBtn, setOrderType;
    private RecyclerView itemList;
    private CartViewModel cartViewModel;
    private LottieAnimationView loadingAnim;
    private int orderType=0;

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
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        setListeners();
        fetchItems();
        observeOrderType();
    }

    private void bindViews(View view) {
        itemList = view.findViewById(R.id.item_list);
        summary = view.findViewById(R.id.summary_layout);
        totalQuantity = view.findViewById(R.id.total_quantity);
        total = view.findViewById(R.id.total);
        complete = view.findViewById(R.id.complete_order);
        menuBtn = view.findViewById(R.id.menu_button);
        loadingAnim = view.findViewById(R.id.loading_anim);
        errorMessage = view.findViewById(R.id.error_message);
        setOrderType = view.findViewById(R.id.set_order_type);
    }

    private void initializeList() {
        itemList.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ItemAdapter(requireContext(), this::itemSelected);
        itemList.setAdapter(adapter);
        itemList.addItemDecoration(new GridSpacingItemDecoration(2, 8, true));
    }

    private void setListeners() {
        complete.setOnClickListener(v -> toCart());
        menuBtn.setOnClickListener(v -> ((CatalogActivity) requireActivity()).openDrawer());
        setOrderType.setOnClickListener(v -> setOrderType());
    }

    private void itemSelected(Item item) {
        ((CatalogActivity) requireActivity()).toCartItem(item);
    }

    private void computeTotals(List<Item> itemsInCart) {
        int count = 0;
        int totalPrice = 0;

        for (Item itemInCart : itemsInCart) {
            count += itemInCart.getQuantity();

            int priceToUse=itemInCart.getPriceWholesale();
            if(orderType==2) priceToUse=itemInCart.getPriceShaba();
            if(orderType==1) priceToUse=itemInCart.getPriceConsignment();

            totalPrice += (itemInCart.getQuantity() * priceToUse);
        }

        if (count > 0) {
            totalQuantity.setText(getString(R.string.total_item_count, count));
            total.setText(getString(R.string.order_total, totalPrice));
            summary.setVisibility(View.VISIBLE);
        } else {
            summary.setVisibility(View.GONE);
        }
    }

    private void toCart() {
        ((CatalogActivity) requireActivity()).toCart();
    }

    private void fetchItems() {
        FirebaseDatabase.getInstance().getReference().child("ItemsV2").get().addOnCompleteListener(task -> {
            hideAnimation();
            if (task.isSuccessful()) {
                List<Item> itemsFromDatabase = new ArrayList<>();
                for (DataSnapshot childSnapshot : task.getResult().getChildren()) {
                    Item coercedItem = childSnapshot.getValue(Item.class);
                    itemsFromDatabase.add(coercedItem);
                }
                adapter.setItems(itemsFromDatabase);
                setQuantityObserver();
            } else {
                errorMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setQuantityObserver() {
        cartViewModel.getCart().observe(getViewLifecycleOwner(), itemsInCart -> {
            computeTotals(itemsInCart);
            calculateQuantities(itemsInCart);
        });
    }

    private void hideAnimation() {
        loadingAnim.pauseAnimation();
        loadingAnim.setVisibility(View.GONE);
    }

    private void calculateQuantities(List<Item> itemsInCart) {
        HashMap<String, Integer> itemQuantities = new HashMap<>();
        for (Item i : itemsInCart) {
            String sku = i.getSku();
            Integer currentQty = itemQuantities.get(sku);
            if (currentQty == null) {
                itemQuantities.put(sku, i.getQuantity());
            } else {
                itemQuantities.put(sku, currentQty + i.getQuantity());
            }
        }
        adapter.updateQuantities(itemQuantities);
    }

    private void observeOrderType() {
        cartViewModel.getOrderType().observe(getViewLifecycleOwner(), orderType -> {
            this.orderType=orderType;
            switch (orderType) {
                case 2:
                    setOrderType.setText(R.string.commission);
                    break;
                case 1:
                    setOrderType.setText(R.string.consignment);
                    break;
                default:
                    setOrderType.setText(R.string.wholesale);
            }
        });
    }

    private void setOrderType() {
        SetOrderTypeDialog dialog = SetOrderTypeDialog.newInstance(cartViewModel);
        dialog.show(getChildFragmentManager(), SetOrderTypeDialog.TAG);
    }
}