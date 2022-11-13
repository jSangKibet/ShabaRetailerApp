package com.acework.shabaretailer.catalog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.adapter.ItemAdapter;
import com.acework.shabaretailer.model.Item;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CatalogFragment extends Fragment {
    private TextInputEditText searchField;
    private ItemAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView itemListView = view.findViewById(R.id.item_list);
        itemListView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ItemAdapter(requireContext(), this::itemSelected);
        itemListView.setAdapter(adapter);
        FirebaseDatabase shabaRealtimeDb = FirebaseDatabase.getInstance();
        DatabaseReference shabaRealtimeDbRef = shabaRealtimeDb.getReference();
        shabaRealtimeDbRef.child("Items").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Item> itemsFromDatabase = new ArrayList<>();
                for (DataSnapshot childSnapshot : task.getResult().getChildren()) {
                    Item coercedItem = childSnapshot.getValue(Item.class);
                    itemsFromDatabase.add(coercedItem);
                }
                adapter.setItems(itemsFromDatabase);
            } else {
                Snackbar.make(requireView(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
        searchField = view.findViewById(R.id.search_field);
        setSearchFunctionality();
    }

    private void itemSelected(Item item) {
        Toast.makeText(requireContext(), "Item selected: " + item.getName(), Toast.LENGTH_SHORT).show();
    }

    private void setSearchFunctionality() {
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s.toString());
            }
        });
    }
}