package com.acework.shabaretailer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.acework.shabaretailer.model.Retailer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NavigationFragment extends Fragment {
    private TextView name, businessName, telephone;
    private final ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            loadUser();
        }
    });
    private MaterialButton toMyOrders, viewTc, logout, setNumber, edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setListeners();
        loadUser();
    }

    private void bindViews(View view) {
        name = view.findViewById(R.id.name);
        businessName = view.findViewById(R.id.business_name);
        telephone = view.findViewById(R.id.telephone);
        toMyOrders = view.findViewById(R.id.to_my_orders);
        viewTc = view.findViewById(R.id.view_tc);
        logout = view.findViewById(R.id.logout);
        setNumber = view.findViewById(R.id.set_number);
        edit = view.findViewById(R.id.edit);
    }

    private void setListeners() {
        toMyOrders.setOnClickListener(v -> startActivity(new Intent(requireContext(), MyOrdersActivity.class)));
        viewTc.setOnClickListener(v -> viewTc());
        logout.setOnClickListener(v -> logoutButtonClicked());
        setNumber.setOnClickListener(v -> startActivity(new Intent(requireContext(), ChangeNumberActivity.class)));
        edit.setOnClickListener(v -> startActivityForResult.launch(new Intent(requireContext(), EditRetailerActivity.class)));
    }

    @SuppressWarnings("ConstantConditions")
    private void loadUser() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference shabaRtDbRef = FirebaseDatabase.getInstance().getReference().child("Retailers/" + uid);
        shabaRtDbRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Retailer retailer = task.getResult().getValue(Retailer.class);
                setValues(retailer);
            } else {
                task.getException().printStackTrace();
            }
        });
    }

    private void setValues(Retailer retailer) {
        name.setText(retailer.getName());
        businessName.setText(retailer.getBusinessName());
        telephone.setText(retailer.getTelephone());
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