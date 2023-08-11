package com.acework.shabaretailer.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.databinding.ActivityChooseInsertColorsBinding;

public class ChooseInsertColorsActivity extends AppCompatActivity {
    private ActivityChooseInsertColorsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseInsertColorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}