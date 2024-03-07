package com.acework.shabaretailer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.acework.shabaretailer.ui.activities.catalog.CatalogActivity
import com.acework.shabaretailer.ui.activities.retailer.WelcomeActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, WelcomeActivity::class.java))
        } else {
            startActivity(Intent(this, CatalogActivity::class.java))
        }
        finish()
    }
}