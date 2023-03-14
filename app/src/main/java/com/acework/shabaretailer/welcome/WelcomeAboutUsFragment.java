package com.acework.shabaretailer.welcome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.WelcomeActivity;
import com.google.android.material.button.MaterialButton;

public class WelcomeAboutUsFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_about_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton s = view.findViewById(R.id.skip);
        MaterialButton n = view.findViewById(R.id.next);
        s.setOnClickListener(v -> ((WelcomeActivity) requireActivity()).toLogin());
        n.setOnClickListener(v -> ((WelcomeActivity) requireActivity()).toRetailer());
    }
}
