package com.acework.shabaretailer;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.acework.shabaretailer.catalog.CartFragment;
import com.acework.shabaretailer.catalog.CartItemFragment;
import com.acework.shabaretailer.catalog.CatalogFragment;
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CatalogActivity extends AppCompatActivity {
    private CatalogFragment catalogFragment;
    private CartItemFragment cartItemFragment;
    private CartFragment cartFragment;
    private Fragment activeFragment;
    private CartViewModel cartViewModel;
    private boolean fromCatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        loadItems();
        initializeFragments();
    }

    public void toCatalog() {
        getSupportFragmentManager().beginTransaction().hide(activeFragment).show(catalogFragment).commit();
        activeFragment = catalogFragment;
    }

    public void toCartItem(Item item, boolean fromCatalog) {
        this.fromCatalog = fromCatalog;
        cartItemFragment.setItem(item);
        getSupportFragmentManager().beginTransaction().hide(activeFragment).show(cartItemFragment).commit();
        activeFragment = cartItemFragment;
    }

    public void toCart() {
        getSupportFragmentManager().beginTransaction().hide(activeFragment).show(cartFragment).commit();
        activeFragment = cartFragment;
    }

    public void backFromCatalog() {
        if (fromCatalog) {
            toCatalog();
        } else {
            toCart();
        }
    }

    private void loadItems() {
        DatabaseReference shabaRealtimeDbRef = FirebaseDatabase.getInstance().getReference().child("Items");
        ValueEventListener itemsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Item> itemsFromDatabase = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Item coercedItem = childSnapshot.getValue(Item.class);
                    itemsFromDatabase.add(coercedItem);
                }
                cartViewModel.setCartItems(itemsFromDatabase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("LF", error.getDetails());
            }
        };
        shabaRealtimeDbRef.addValueEventListener(itemsListener);
    }

    private void initializeFragments() {
        catalogFragment = new CatalogFragment();
        cartItemFragment = new CartItemFragment();
        cartFragment = new CartFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, catalogFragment)
                .add(R.id.fragment_container, cartItemFragment)
                .add(R.id.fragment_container, cartFragment)
                .hide(cartItemFragment)
                .hide(cartFragment)
                .show(catalogFragment)
                .commit();
        activeFragment = catalogFragment;
    }
}