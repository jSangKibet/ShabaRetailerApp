package com.acework.shabaretailer;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.acework.shabaretailer.atlas.Atlas;
import com.acework.shabaretailer.catalog.CartFragment;
import com.acework.shabaretailer.catalog.CartItemFragment;
import com.acework.shabaretailer.catalog.CatalogFragment;
import com.acework.shabaretailer.catalog.ConfirmOrderFragment;
import com.acework.shabaretailer.model.Retailer;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CatalogActivity extends AppCompatActivity {
    private CatalogFragment catalogFragment;
    private CartItemFragment cartItemFragment;
    private CartFragment cartFragment;
    private ConfirmOrderFragment confirmOrderFragment;
    private Fragment activeFragment;
    private CartViewModel cartViewModel;
    private DrawerLayout navDrawer;
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

    public void toCartItem(String sku) {
        fromCatalog = activeFragment == catalogFragment;
        cartItemFragment.setItem(sku);
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
        confirmOrderFragment.uncheckTC();
    }

    private void loadItems() {
        cartViewModel.getItemsInCartLive().observe(this, itemsInCart -> {
            if (Atlas.getItemsInCart(itemsInCart).size() == 0) {
                if (activeFragment == cartFragment) {
                    onBackPressed();
                }
            }
        });

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u != null) {
            FirebaseFirestore.getInstance().collection("retailers").document(u.getUid()).addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    Log.w("C/FS", "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Retailer r = snapshot.toObject(Retailer.class);
                    cartViewModel.setRetailer(r);
                } else {
                    Log.d("C/FS", "Retailer not found");
                }
            });
        }
    }

    private void initializeFragments() {
        catalogFragment = new CatalogFragment();
        cartItemFragment = new CartItemFragment();
        cartFragment = new CartFragment();
        confirmOrderFragment = new ConfirmOrderFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, catalogFragment).add(R.id.fragment_container, cartItemFragment).add(R.id.fragment_container, cartFragment).add(R.id.fragment_container, confirmOrderFragment).hide(cartItemFragment).hide(cartFragment).hide(confirmOrderFragment).show(catalogFragment).commit();
        activeFragment = catalogFragment;
    }

    public void orderCompleted() {
        cartViewModel.resetCart();
        toCatalog();
    }

    public void openDrawer() {
        navDrawer.openDrawer(GravityCompat.START);
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