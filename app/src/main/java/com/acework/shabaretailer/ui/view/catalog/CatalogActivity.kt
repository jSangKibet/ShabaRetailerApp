package com.acework.shabaretailer.ui.view.catalog

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.acework.shabaretailer.databinding.ActivityCatalogBinding
import com.acework.shabaretailer.ui.ItemDetailsActivity
import com.acework.shabaretailer.ui.view.byb.BybIntroActivity

class CatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewWahura.setOnClickListener { viewItem("3") }
        binding.viewTwende.setOnClickListener { viewItem("1") }
        binding.menu.setOnClickListener { binding.navigationDrawer.openDrawer(GravityCompat.START) }


        startActivity((Intent(this, BybIntroActivity::class.java)))
    }

    private fun viewItem(sku: String) {
        val intent = Intent(this, ItemDetailsActivity::class.java)
        intent.putExtra("sku", sku)
        startActivity(intent)
    }
}