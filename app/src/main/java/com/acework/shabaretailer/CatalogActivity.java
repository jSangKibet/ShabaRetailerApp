package com.acework.shabaretailer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.catalog.CatalogFragment;

public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        toCatalog();
    }

    private void toCatalog() {
        CatalogFragment catalogFragment = new CatalogFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, catalogFragment).commit();
    }
}