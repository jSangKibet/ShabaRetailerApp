package com.acework.shabaretailer.catalog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.atlas.Atlas;
import com.acework.shabaretailer.databinding.FragmentCartItemBinding;
import com.acework.shabaretailer.model.ItemInCart;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CartItemFragment extends Fragment {
    private FragmentCartItemBinding binding;
    private ItemInCart item;
    private CartViewModel cartViewModel;
    private LayoutInflater layoutInflater;
    private ImageFragmentAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartItemBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        layoutInflater = LayoutInflater.from(requireContext());
        setListeners();
    }

    private void setValues() {
        binding.name.setText(item.getItem().getName());
        binding.mustardQty.setText(String.valueOf(item.getMustardInsertNum()));
        binding.maroonQty.setText(String.valueOf(item.getMaroonInsertNum()));
        binding.darkBrownQty.setText(String.valueOf(item.getDarkBrownInsertNum()));
        binding.totalQuantity.setText(getString(R.string.quantity_ph, item.getQuantity()));
        setTotal();
        setItemDetails();

        switch (cartViewModel.getOrderType()) {
            case 2:
                binding.price.setText(getString(R.string.price, item.getItem().getPriceShaba()));
                break;
            case 1:
                binding.price.setText(getString(R.string.price, item.getItem().getPriceConsignment()));
                break;
            default:
                binding.price.setText(getString(R.string.price, item.getItem().getPriceWholesale()));

        }
    }

    private void setTotal() {
        int itemTotal = 0;
        int priceToUse = Atlas.getPriceToUse(item.getItem(), cartViewModel.getOrderType());
        itemTotal += item.getMustardInsertNum() * priceToUse;
        itemTotal += item.getMaroonInsertNum() * priceToUse;
        itemTotal += item.getDarkBrownInsertNum() * priceToUse;
        binding.total.setText(getString(R.string.total, itemTotal));
    }

    private void setItemDetails() {
        binding.description.setText(item.getItem().getDescription());
        binding.size.setText(item.getItem().getSize());
        binding.material.setText(item.getItem().getMaterial());
        binding.weaving.setText(item.getItem().getWeaving());
        binding.color.setText(item.getItem().getColour());
        binding.strap.setText(item.getItem().getStrap());
        binding.insert.setText(item.getItem().getInsert());
        binding.weight.setText(getString(R.string.weight_formatted, item.getItem().getWeight()));
        binding.sku.setText(item.getItem().getSku());
        binding.strapLength.setText(item.getItem().getStrapLength());
        setFeatures();
    }

    private void setFeatures() {
        binding.features.removeAllViews();
        for (String feature : item.getItem().getFeatures()) {
            @SuppressLint("InflateParams")
            TextView textView = (TextView) layoutInflater.inflate(R.layout.view_textview, null);
            textView.setText(getString(R.string.bullet_item, feature));
            binding.features.addView(textView);
        }
    }

    private void setListeners() {
        binding.backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.done.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.mustardMinus.setOnClickListener(v -> decrement("Mustard", 1));
        binding.mustardMinus5.setOnClickListener(v -> decrement("Mustard", 5));
        binding.mustardPlus.setOnClickListener(v -> increment("Mustard", 1));
        binding.mustardPlus5.setOnClickListener(v -> increment("Mustard", 5));
        binding.maroonMinus.setOnClickListener(v -> decrement("Maroon", 1));
        binding.maroonMinus5.setOnClickListener(v -> decrement("Maroon", 5));
        binding.maroonPlus.setOnClickListener(v -> increment("Maroon", 1));
        binding.maroonPlus5.setOnClickListener(v -> increment("Maroon", 5));
        binding.darkBrownMinus.setOnClickListener(v -> decrement("Dark brown", 1));
        binding.darkBrownMinus5.setOnClickListener(v -> decrement("Dark brown", 5));
        binding.darkBrownPlus.setOnClickListener(v -> increment("Dark brown", 1));
        binding.darkBrownPlus5.setOnClickListener(v -> increment("Dark brown", 5));
        binding.more.setOnClickListener(v -> binding.moreLayout.setVisibility(View.VISIBLE));
        binding.less.setOnClickListener(v -> binding.moreLayout.setVisibility(View.GONE));
        binding.description.setOnClickListener(v -> showDescription());
    }

    private void decrement(String insert, int quantity) {
        if (insert.equals("Mustard")) {
            item.decrementMustard(quantity);
        } else if (insert.equals("Maroon")) {
            item.decrementMaroon(quantity);
        } else {
            item.decrementDarkBrown(quantity);
        }
        cartViewModel.commit();
        setValues();
    }

    private void increment(String insert, int quantity) {
        if (insert.equals("Mustard")) {
            item.incrementMustard(quantity);
        } else if (insert.equals("Maroon")) {
            item.incrementMaroon(quantity);
        } else {
            item.incrementDarkBrown(quantity);
        }
        cartViewModel.commit();
        setValues();
    }

    public void setItem(String sku) {
        for (ItemInCart itemInCart : cartViewModel.getItemsInCart()) {
            if (itemInCart.getItem().getSku().equals(sku)) {
                this.item = itemInCart;
            }
        }
        binding.moreLayout.setVisibility(View.GONE);
        loadImages();
        setValues();
    }

    private void loadImages() {
        String sku = item.getItem().getSku();
        if (adapter == null || !adapter.getSku().equals(sku)) {
            adapter = new ImageFragmentAdapter(requireActivity(), sku, item.getItem().getName());
            binding.pager.setAdapter(adapter);
            checkIfSwipePromptHasBeenShownRecently();
        }
    }

    private void showDescription() {
        new MaterialAlertDialogBuilder(requireContext()).setTitle("Description").setMessage(item.getItem().getDescription()).setPositiveButton("Close", null).show();
    }

    private void checkIfSwipePromptHasBeenShownRecently() {
        SharedPreferences sp = requireActivity().getSharedPreferences("shaba_retailers", Context.MODE_PRIVATE);
        long lastShown = sp.getLong("swipe_prompt_last_shown", 0);
        if (System.currentTimeMillis() - lastShown > 604800000L) showSwipePrompt(sp);
    }

    private void showSwipePrompt(SharedPreferences sp) {
        binding.swipeAnimation.setVisibility(View.VISIBLE);
        binding.swipeAnimation.playAnimation();
        sp.edit().putLong("swipe_prompt_last_shown", System.currentTimeMillis()).apply();
        binding.pager.postDelayed(() -> binding.swipeAnimation.setVisibility(View.GONE), 3000);
    }

    private static class ImageFragmentAdapter extends FragmentStateAdapter {
        private final String sku;
        private final String name;

        public ImageFragmentAdapter(FragmentActivity fa, String sku, String name) {
            super(fa);
            this.sku = sku;
            this.name = name;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Bundle args = new Bundle();
            args.putString("sku", sku);
            args.putInt("pos", position + 1);
            args.putString("name", name);
            ImageFragment imgF = new ImageFragment();
            imgF.setArguments(args);
            return imgF;
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        public String getSku() {
            return sku;
        }
    }
}