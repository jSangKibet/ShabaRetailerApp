package com.acework.shabaretailer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.databinding.ActivityBuildYourBoxBinding;

public class BuildYourBoxActivity extends AppCompatActivity {
    ActivityBuildYourBoxBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityBuildYourBoxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}