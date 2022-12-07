package com.acework.shabaretailer.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CartItemFragment extends Fragment {
    private Item item, itemMustard, itemMaroon, itemDarkBrown;
    private TextView itemName, price, quantityMustard, quantityMaroon, quantityDarkBrown, total;
    private MaterialButton back, done, mustardMinus5, mustardMinus, mustardPlus, mustardPlus5;
    private MaterialButton maroonMinus5, maroonMinus, maroonPlus, maroonPlus5;
    private MaterialButton darkBrownMinus5, darkBrownMinus, darkBrownPlus, darkBrownPlus5;
    private ImageView imageView;

    private TextView description, size, material, weaving, color, strap, insert, weight, sku, strapLength;
    private LinearLayout features;
    private MaterialButton more, less;
    private ConstraintLayout moreLayout;

    private CartViewModel cartViewModel;
    private LayoutInflater layoutInflater;

    public CartItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        layoutInflater = LayoutInflater.from(requireContext());
        bindViews(view);
        setListeners();
    }

    private void bindViews(View view) {
        back = view.findViewById(R.id.back_button);
        itemName = view.findViewById(R.id.name);
        price = view.findViewById(R.id.price);
        quantityMustard = view.findViewById(R.id.mustard_qty);
        quantityMaroon = view.findViewById(R.id.maroon_qty);
        quantityDarkBrown = view.findViewById(R.id.dark_brown_qty);
        total = view.findViewById(R.id.total);
        done = view.findViewById(R.id.done);
        imageView = view.findViewById(R.id.image);
        mustardMinus = view.findViewById(R.id.mustard_minus);
        mustardMinus5 = view.findViewById(R.id.mustard_minus_5);
        mustardPlus = view.findViewById(R.id.mustard_plus);
        mustardPlus5 = view.findViewById(R.id.mustard_plus_5);
        maroonMinus = view.findViewById(R.id.maroon_minus);
        maroonMinus5 = view.findViewById(R.id.maroon_minus_5);
        maroonPlus = view.findViewById(R.id.maroon_plus);
        maroonPlus5 = view.findViewById(R.id.maroon_plus_5);
        darkBrownMinus = view.findViewById(R.id.dark_brown_minus);
        darkBrownMinus5 = view.findViewById(R.id.dark_brown_minus_5);
        darkBrownPlus = view.findViewById(R.id.dark_brown_plus);
        darkBrownPlus5 = view.findViewById(R.id.dark_brown_plus_5);
        description = view.findViewById(R.id.description);
        size = view.findViewById(R.id.size);
        material = view.findViewById(R.id.material);
        weaving = view.findViewById(R.id.weaving);
        color = view.findViewById(R.id.color);
        strap = view.findViewById(R.id.strap);
        insert = view.findViewById(R.id.insert);
        weight = view.findViewById(R.id.weight);
        sku = view.findViewById(R.id.sku);
        strapLength = view.findViewById(R.id.strap_length);
        features = view.findViewById(R.id.features);
        more = view.findViewById(R.id.more);
        less = view.findViewById(R.id.less);
        moreLayout = view.findViewById(R.id.more_layout);
    }

    private void setValues() {
        itemName.setText(item.getName());
        price.setText(getString(R.string.price, item.getPrice()));
        quantityMustard.setText(String.valueOf(itemMustard.getQuantity()));
        quantityMaroon.setText(String.valueOf(itemMaroon.getQuantity()));
        quantityDarkBrown.setText(String.valueOf(itemDarkBrown.getQuantity()));
        setTotal();
        setItemDetails();
    }

    private void setTotal() {
        int totalInt = itemMustard.getPrice() * itemMustard.getQuantity();
        totalInt += (itemMaroon.getPrice() * itemMaroon.getQuantity());
        totalInt += (itemDarkBrown.getPrice() * itemDarkBrown.getQuantity());
        total.setText(getString(R.string.total, totalInt));
    }

    private void setItemDetails() {
        description.setText(item.getDescription());
        size.setText(item.getSize());
        material.setText(item.getMaterial());
        weaving.setText(item.getWeaving());
        color.setText(item.getColour());
        strap.setText(item.getStrap());
        insert.setText(item.getInsert());
        weight.setText(getString(R.string.weight_formatted, item.getWeight()));
        sku.setText(item.getSku());
        strapLength.setText(item.getStrapLength());
        setFeatures();
    }

    private void setFeatures() {
        features.removeAllViews();
        for (String feature : item.getFeatures()) {
            TextView textView = (TextView) layoutInflater.inflate(R.layout.view_textview, null);
            textView.setText(getString(R.string.bullet_item, feature));
            features.addView(textView);
        }
    }

    private void setListeners() {
        back.setOnClickListener(v -> requireActivity().onBackPressed());
        done.setOnClickListener(v -> done());
        mustardMinus.setOnClickListener(v -> decrementByOne(itemMustard));
        mustardMinus5.setOnClickListener(v -> decrementByFive(itemMustard));
        mustardPlus.setOnClickListener(v -> incrementByOne(itemMustard));
        mustardPlus5.setOnClickListener(v -> incrementByFive(itemMustard));
        maroonMinus.setOnClickListener(v -> decrementByOne(itemMaroon));
        maroonMinus5.setOnClickListener(v -> decrementByFive(itemMaroon));
        maroonPlus.setOnClickListener(v -> incrementByOne(itemMaroon));
        maroonPlus5.setOnClickListener(v -> incrementByFive(itemMaroon));
        darkBrownMinus.setOnClickListener(v -> decrementByOne(itemDarkBrown));
        darkBrownMinus5.setOnClickListener(v -> decrementByFive(itemDarkBrown));
        darkBrownPlus.setOnClickListener(v -> incrementByOne(itemDarkBrown));
        darkBrownPlus5.setOnClickListener(v -> incrementByFive(itemDarkBrown));
        more.setOnClickListener(v -> moreLayout.setVisibility(View.VISIBLE));
        less.setOnClickListener(v -> moreLayout.setVisibility(View.GONE));
        description.setOnClickListener(v -> showDescription());
    }

    private void decrementByOne(Item itemToDecrement) {
        if (itemToDecrement.getQuantity() > 0) {
            itemToDecrement.setQuantity(itemToDecrement.getQuantity() - 1);
        }
        setValues();
    }

    private void decrementByFive(Item itemToDecrement) {
        if (itemToDecrement.getQuantity() > 0) {
            int newQuantity = itemToDecrement.getQuantity() - 5;
            if (newQuantity < 0) newQuantity = 0;
            itemToDecrement.setQuantity(newQuantity);
        }
        setValues();
    }

    private void incrementByOne(Item itemToDecrement) {
        if (itemToDecrement.getQuantity() < 30) {
            itemToDecrement.setQuantity(itemToDecrement.getQuantity() + 1);
        }
        setValues();
    }

    private void incrementByFive(Item itemToDecrement) {
        if (itemToDecrement.getQuantity() < 30) {
            int newQuantity = itemToDecrement.getQuantity() + 5;
            if (newQuantity > 30) newQuantity = 30;
            itemToDecrement.setQuantity(newQuantity);
        }
        setValues();
    }

    public void setItem(Item item) {
        this.item = item;
        moreLayout.setVisibility(View.GONE);
        loadImage();
        getItems(item);
        setValues();
    }

    private void loadImage() {
        String imageName = item.getSku() + "_01.jpg";
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference shabaCSR = firebaseStorage.getReference().child("item_images");
        shabaCSR.child(imageName).getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(imageView).load(task.getResult()).placeholder(R.drawable.image_96).into(imageView);
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.image_96));
            }
        });
    }

    private void done() {
        cartViewModel.setItem(itemMustard);
        cartViewModel.setItem(itemMaroon);
        cartViewModel.setItem(itemDarkBrown);
        requireActivity().onBackPressed();
    }

    private void getItems(Item item) {
        itemMustard = cartViewModel.getItemFromCart(item.getSku(), "Mustard");
        if (itemMustard == null) itemMustard = item.cloneItem("Mustard");
        itemMaroon = cartViewModel.getItemFromCart(item.getSku(), "Maroon");
        if (itemMaroon == null) itemMaroon = item.cloneItem("Maroon");
        itemDarkBrown = cartViewModel.getItemFromCart(item.getSku(), "Dark brown");
        if (itemDarkBrown == null) itemDarkBrown = item.cloneItem("Dark brown");
    }

    private void showDescription() {
        new MaterialAlertDialogBuilder(requireContext()).setTitle("Description").setMessage(item.getDescription()).setPositiveButton("Close", null).show();
    }
}