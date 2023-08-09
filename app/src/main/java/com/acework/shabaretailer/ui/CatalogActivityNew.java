package com.acework.shabaretailer.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.adapter.ItemAdapterNew;
import com.acework.shabaretailer.databinding.ActivityCatalogNewBinding;
import com.acework.shabaretailer.model.ItemNew;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CatalogActivityNew extends AppCompatActivity {
    ActivityCatalogNewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCatalogNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ItemAdapterNew adapter = new ItemAdapterNew(this, this::viewItem);
        binding.list.setAdapter(adapter);
        fetchItems(adapter);
    }

    private void viewItem(ItemNew item) {

    }

    private void fetchItems(ItemAdapterNew adapter) {
        FirebaseFirestore.getInstance().collection("items").get().addOnCompleteListener(task -> {
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