package com.acework.shabaretailer.ui.view.byb

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.acework.shabaretailer.databinding.ActivityBybIntroBinding

class BybIntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBybIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBybIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.instructions.setOnClickListener {
            startActivity(Intent(this, InstructionsActivity::class.java))
            finish()
        }
    }
}