package com.afaryn.imunisasiku.presentation.imunisasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.imunisasiku.databinding.ActivityImunisasiKuBinding

class ImunisasiKuActivity : AppCompatActivity() {

    private var _binding: ActivityImunisasiKuBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityImunisasiKuBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}