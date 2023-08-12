package com.acework.shabaretailer.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.databinding.ActivityConfirmOrderBinding;

public class ConfirmOrderActivity extends AppCompatActivity {
    ActivityConfirmOrderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}