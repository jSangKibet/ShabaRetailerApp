package com.acework.shabaretailer.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CartItemFragment extends Fragment {
    private Item item;
    private TextView itemName, price, minus5, minus, quantity, plus, plus5, total, size, weaving, strap, weight, sku;
    private MaterialButton back, done;
    private ImageView imageView;

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
        bindViews(view);
        setListeners();
    }

    private void bindViews(View view) {
        back = view.findViewById(R.id.back_button);
        itemName = view.findViewById(R.id.name);
        price = view.findViewById(R.id.price);
        minus5 = view.findViewById(R.id.minus_5);
        minus = view.findViewById(R.id.minus);
        quantity = view.findViewById(R.id.quantity);
        plus = view.findViewById(R.id.plus);
        plus5 = view.findViewById(R.id.plus_5);
        total = view.findViewById(R.id.total);
        size = view.findViewById(R.id.size);
        weaving = view.findViewById(R.id.weaving);
        strap = view.findViewById(R.id.strap);
        weight = view.findViewById(R.id.weight);
        sku = view.findViewById(R.id.sku);
        done = view.findViewById(R.id.done);
        imageView = view.findViewById(R.id.image);
    }

    private void setValues() {
        itemName.setText(item.getName());
        price.setText(getString(R.string.price, item.getPrice()));
        quantity.setText(String.valueOf(item.getQuantity()));
        setTotal(item);
        size.setText(item.getSize());
        weaving.setText(item.getWeaving());
        strap.setText(item.getStrapLength());
        weight.setText(getString(R.string.weight_formatted, item.getWeight()));
        sku.setText(item.getSku());
        loadImage();
    }

    private void setTotal(Item item) {
        int totalInt = item.getPrice() * item.getQuantity();
        total.setText(getString(R.string.total, totalInt));
    }

    private void setListeners() {
        minus5.setOnClickListener(v -> decrementByFive());
        minus.setOnClickListener(v -> decrementByOne());
        plus.setOnClickListener(v -> incrementByOne());
        plus5.setOnClickListener(v -> incrementByFive());
        back.setOnClickListener(v -> requireActivity().onBackPressed());
        done.setOnClickListener(v -> done());
    }

    private void decrementByOne() {
        if (item.getQuantity() > 0) {
            item.setQuantity(item.getQuantity() - 1);
        }
        setValues();
    }

    private void decrementByFive() {
        if (item.getQuantity() > 0) {
            int newQuantity = item.getQuantity() - 5;
            if (newQuantity < 0) newQuantity = 0;
            item.setQuantity(newQuantity);
        }
        setValues();
    }

    private void incrementByOne() {
        if (item.getQuantity() < 30) {
            item.setQuantity(item.getQuantity() + 1);
        }
        setValues();
    }

    private void incrementByFive() {
        if (item.getQuantity() < 30) {
            int newQuantity = item.getQuantity() + 5;
            if (newQuantity > 30) newQuantity = 30;
            item.setQuantity(newQuantity);
        }
        setValues();
    }

    public void setItem(Item item) {
        this.item = item.cloneItem();
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
        CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.setQuantity(item.getSku(), item.getQuantity());
        requireActivity().onBackPressed();
    }
}