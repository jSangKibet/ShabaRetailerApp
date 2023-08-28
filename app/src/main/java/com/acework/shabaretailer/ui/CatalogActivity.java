package com.acework.shabaretailer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.acework.shabaretailer.adapter.ItemAdapterNew;
import com.acework.shabaretailer.databinding.ActivityCatalogBinding;
import com.acework.shabaretailer.model.ItemNew;
import com.acework.shabaretailer.ui.view.byb.BybIntroActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CatalogActivity extends AppCompatActivity {
    ActivityCatalogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCatalogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ItemAdapterNew adapter = new ItemAdapterNew(this, this::viewItem, getLifecycle());
        binding.list.setAdapter(adapter);
        fetchItems(adapter);

        binding.menu.setOnClickListener(v -> binding.navigationDrawer.openDrawer(GravityCompat.START));
        binding.placeOrder.setOnClickListener(v -> startActivity(new Intent(this, BuildYourBoxActivity.class)));

        startActivity(new Intent(this, BybIntroActivity.class));
    }

    private void viewItem(String sku) {
        Intent intent = new Intent(this, ItemDetailsActivity.class);
        intent.putExtra("sku", sku);
        startActivity(intent);
    }

    private void fetchItems(ItemAdapterNew adapter) {
        FirebaseFirestore.getInstance().collection("items").whereEqualTo("active", true).get().addOnCompleteListener(task -> {
            binding.loading.pauseAnimation();
            binding.loading.setVisibility(View.GONE);

            if (task.isSuccessful()) {
                List<ItemNew> itemsFromDb = new ArrayList<>();
                for (QueryDocumentSnapshot qds : task.getResult()) {
                    ItemNew i = qds.toObject(ItemNew.class);
                    itemsFromDb.add(i);
                }
                adapter.setItems(itemsFromDb);
            } else {
                binding.errorMessage.setVisibility(View.VISIBLE);
                if (task.getException() != null) task.getException().printStackTrace();
            }
        });
    }
}