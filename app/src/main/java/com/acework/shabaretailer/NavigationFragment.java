package com.acework.shabaretailer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.acework.shabaretailer.model.Retailer;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NavigationFragment extends Fragment {
    private TextView name, businessName, telephone;
    private MaterialButton toMyOrders, viewTc;

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
        viewTc=view.findViewById(R.id.view_tc);

    }

    private void setListeners() {
        toMyOrders.setOnClickListener(v -> startActivity(new Intent(requireContext(), MyOrdersActivity.class)));
        viewTc.setOnClickListener(v -> viewTc());
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

    private void viewTc(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.theshaba.com/terms-of-use"));
        startActivity(browserIntent);
    }
}