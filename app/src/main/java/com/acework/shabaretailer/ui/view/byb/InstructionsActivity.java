package com.acework.shabaretailer.ui.view.byb;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.databinding.ActivityInstructionsBinding;

public class InstructionsActivity extends AppCompatActivity {
    private ActivityInstructionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInstructionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.proceed.setOnClickListener(v -> finish());
        binding.top.setOnClickListener(v -> binding.getRoot().fullScroll(View.FOCUS_UP));
    }
}