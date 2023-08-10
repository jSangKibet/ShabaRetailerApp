package com.acework.shabaretailer.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.databinding.ActivityPlaceOrderBinding;

public class PlaceOrderActivity extends AppCompatActivity {
    ActivityPlaceOrderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}