package com.acework.shabaretailer;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import com.acework.shabaretailer.catalog.CartFragment;
import com.acework.shabaretailer.catalog.CartItemFragment;
import com.acework.shabaretailer.catalog.CatalogFragment;
import com.acework.shabaretailer.catalog.ConfirmOrderFragment;
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.snackbar.Snackbar;
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
    private ConfirmOrderFragment confirmOrderFragment;
    private Fragment activeFragment;
    private CartViewModel cartViewModel;
    private boolean fromCatalog;
    private DrawerLayout navDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        navDrawer = findViewById(R.id.navigation_drawer);
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

    public void toConfirmOrder() {
        getSupportFragmentManager().beginTransaction().hide(activeFragment).show(confirmOrderFragment).commit();
        activeFragment = confirmOrderFragment;
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
        confirmOrderFragment = new ConfirmOrderFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, catalogFragment)
                .add(R.id.fragment_container, cartItemFragment)
                .add(R.id.fragment_container, cartFragment)
                .add(R.id.fragment_container, confirmOrderFragment)
                .hide(cartItemFragment)
                .hide(cartFragment)
                .hide(confirmOrderFragment)
                .show(catalogFragment)
                .commit();
        activeFragment = catalogFragment;
    }

    public void orderCompleted() {
        FragmentContainerView view = findViewById(R.id.fragment_container);
        cartViewModel.resetCart();
        toCatalog();
        Snackbar.make(view, "Order completed!", Snackbar.LENGTH_LONG).show();
    }

    public void openDrawer() {
        navDrawer.openDrawer(GravityCompat.START);
    }
}