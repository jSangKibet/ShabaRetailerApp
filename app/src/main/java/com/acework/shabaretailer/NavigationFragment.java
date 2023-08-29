package com.acework.shabaretailer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.acework.shabaretailer.databinding.FragmentNavigationBinding;
import com.acework.shabaretailer.dialog.BankDetailsDialog;
import com.acework.shabaretailer.ui.MyOrdersActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class NavigationFragment extends Fragment {
    FragmentNavigationBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNavigationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
        loadUser();
    }


    private void setListeners() {
        binding.toMyOrders.setOnClickListener(v -> startActivity(new Intent(requireContext(), MyOrdersActivity.class)));
        binding.viewTc.setOnClickListener(v -> viewTc());
        binding.logout.setOnClickListener(v -> logoutButtonClicked());
        binding.edit.setOnClickListener(v -> startActivity(new Intent(requireContext(), EditRetailerActivity.class)));
        binding.bankDetails.setOnClickListener(v -> {
            BankDetailsDialog d = new BankDetailsDialog();
            d.show(getChildFragmentManager(), BankDetailsDialog.TAG);
        });
    }

    private void loadUser() {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u != null) {
            binding.email.setText(u.getEmail());
            FirebaseFirestore.getInstance().collection("retailers").document(u.getUid()).addSnapshotListener((value, error) -> {
                if (value != null) {
                    binding.name.setText((String) value.get("name"));
                }
                if (error != null) {
                    error.printStackTrace();
                }
            });
        }
    }

    private void viewTc() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.theshaba.com/terms-of-use"));
        startActivity(browserIntent);
    }

    private void logoutButtonClicked() {
        new MaterialAlertDialogBuilder(requireContext()).
                setTitle("Confirm logging out").
                setMessage("Do you want to log out of Shaba Retailers?").
                setPositiveButton("Yes", (dialog, which) -> logout()).
                setNegativeButton("Cancel", null).show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(requireContext(), LoginActivity.class));
        requireActivity().finish();
    }
}