package com.acework.shabaretailer.catalog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class CartItemFragment extends Fragment {
    private FragmentCartItemBinding binding;
    private ItemInCart item;
    private CartViewModel cartViewModel;
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
        setListeners();
    }

    private void setValues() {
        if (item.getItem().getSku().equals("2")) {
            binding.dustyPinkLayout.setVisibility(View.GONE);
            binding.taupeLayout.setVisibility(View.GONE);
        } else {
            binding.dustyPinkLayout.setVisibility(View.VISIBLE);
            binding.taupeLayout.setVisibility(View.VISIBLE);
        }
        binding.name.setText(item.getItem().getName());
        binding.mustardQty.setText(String.valueOf(item.getMustardInsertNum()));
        binding.maroonQty.setText(String.valueOf(item.getMaroonInsertNum()));
        binding.darkBrownQty.setText(String.valueOf(item.getDarkBrownInsertNum()));
        binding.dustyPinkQty.setText(String.valueOf(item.getDustyPinkInsertNum()));
        binding.taupeQty.setText(String.valueOf(item.getTaupeInsertNum()));
        binding.totalQuantity.setText(getString(R.string.quantity_ph, item.getQuantity()));
        setTotal();

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
        itemTotal += item.getDustyPinkInsertNum() * priceToUse;
        itemTotal += item.getTaupeInsertNum() * priceToUse;
        binding.total.setText(getString(R.string.total, itemTotal));
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
        binding.dustyPinkMinus.setOnClickListener(v -> decrement("Dusty pink", 1));
        binding.dustyPinkMinus5.setOnClickListener(v -> decrement("Dusty pink", 5));
        binding.dustyPinkPlus.setOnClickListener(v -> increment("Dusty pink", 1));
        binding.dustyPinkPlus5.setOnClickListener(v -> increment("Dusty pink", 5));
        binding.taupeMinus.setOnClickListener(v -> decrement("Taupe", 1));
        binding.taupeMinus5.setOnClickListener(v -> decrement("Taupe", 5));
        binding.taupePlus.setOnClickListener(v -> increment("Taupe", 1));
        binding.taupePlus5.setOnClickListener(v -> increment("Taupe", 5));
        binding.more.setOnClickListener(v -> showDescription());
    }

    private void decrement(String insert, int quantity) {
        switch (insert) {
            case "Dark brown":
                item.decrementDarkBrown(quantity);
                break;
            case "Maroon":
                item.decrementMaroon(quantity);
                break;
            case "Mustard":
                item.decrementMustard(quantity);
                break;
            case "Dusty pink":
                item.decrementDustyPink(quantity);
                break;
            default:
                item.decrementTaupe(quantity);
        }
        cartViewModel.commit();
        setValues();
    }

    private void increment(String insert, int quantity) {
        switch (insert) {
            case "Dark brown":
                item.incrementDarkBrown(quantity);
                break;
            case "Maroon":
                item.incrementMaroon(quantity);
                break;
            case "Mustard":
                item.incrementMustard(quantity);
                break;
            case "Dusty pink":
                item.incrementDustyPink(quantity);
                break;
            default:
                item.incrementTaupe(quantity);
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
        FeaturesDialog dialog = FeaturesDialog.newInstance(item.getItem());
        dialog.show(getChildFragmentManager(), FeaturesDialog.TAG);
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