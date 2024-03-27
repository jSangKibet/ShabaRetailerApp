package com.acework.shabaretailer.ui.activities.retailer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShabaRetailersTheme {
                Welcome {
                    finish()
                }
            }
        }
    }
}