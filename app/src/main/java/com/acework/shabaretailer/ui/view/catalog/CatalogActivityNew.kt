package com.acework.shabaretailer.ui.view.catalog

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.acework.shabaretailer.databinding.ActivityCatalogNewBinding
import com.acework.shabaretailer.ui.ItemDetailsActivity
import com.acework.shabaretailer.ui.view.byb.BybIntroActivity

class CatalogActivityNew : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogNewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewWahura.setOnClickListener { viewItem("3") }
        binding.viewTwende.setOnClickListener { viewItem("1") }

        startActivity((Intent(this, BybIntroActivity::class.java)))
    }

    private fun viewItem(sku: String) {
        val intent = Intent(this, ItemDetailsActivity::class.java)
        intent.putExtra("sku", sku)
        startActivity(intent)
    }
}