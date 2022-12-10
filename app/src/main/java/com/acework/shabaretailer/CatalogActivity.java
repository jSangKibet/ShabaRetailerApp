package com.acework.shabaretailer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.acework.shabaretailer.catalog.CartFragment;
import com.acework.shabaretailer.catalog.CartItemFragment;
import com.acework.shabaretailer.catalog.CatalogFragment;
import com.acework.shabaretailer.catalog.ConfirmOrderFragment;
import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.model.Retailer;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CatalogActivity extends AppCompatActivity {
    private CatalogFragment catalogFragment;
    private CartItemFragment cartItemFragment;
    private CartFragment cartFragment;
    private ConfirmOrderFragment confirmOrderFragment;
    private Fragment activeFragment;
    private CartViewModel cartViewModel;
    private DrawerLayout navDrawer;
    private Retailer retailer;
    private boolean fromCatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        navDrawer = findViewById(R.id.navigation_drawer);
        initializeFragments();
        loadItems();
    }

    public void toCatalog() {
        getSupportFragmentManager().beginTransaction().hide(activeFragment).show(catalogFragment).commit();
        activeFragment = catalogFragment;
    }

    public void toCartItem(Item item) {
        fromCatalog = activeFragment == catalogFragment;
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

    @SuppressWarnings("ConstantConditions")
    private void loadItems() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference shabaRtDbRef = FirebaseDatabase.getInstance().getReference();
        shabaRtDbRef.child("Retailers").child(uid).get().addOnCompleteListener(task -> retailer = task.getResult().getValue(Retailer.class));
        cartViewModel.getCart().observe(this, items -> {
            if (items.size() == 0) {
                if (activeFragment == cartFragment) {
                    onBackPressed();
                }
            }
        });
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
        cartViewModel.resetCart();
        toCatalog();
        Snackbar.make(navDrawer, "Order placed!", Snackbar.LENGTH_LONG).show();
    }

    public void openDrawer() {
        navDrawer.openDrawer(GravityCompat.START);
    }

    public Retailer getRetailer() {
        return retailer;
    }

    @Override
    public void onBackPressed() {
        if (activeFragment == cartItemFragment) {
            if (fromCatalog) {
                toCatalog();
            } else {
                toCart();
            }
            return;
        }

        if (activeFragment == cartFragment) {
            toCatalog();
            return;
        }

        if (activeFragment == confirmOrderFragment) {
            toCart();
            return;
        }

        super.onBackPressed();
    }
}