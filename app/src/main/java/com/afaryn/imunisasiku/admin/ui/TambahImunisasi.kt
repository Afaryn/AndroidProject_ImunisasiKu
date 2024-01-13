package com.afaryn.imunisasiku.admin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.ActivityTambahImunisasiBinding

@Suppress("DEPRECATION")
class TambahImunisasi : AppCompatActivity() {

    private lateinit var binding:ActivityTambahImunisasiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTambahImunisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ToolbarName()
        binding.include.btnBack.setOnClickListener{onBackPressed()}
    }

    private fun ToolbarName(){
        binding.include.tvToolbar.text="Tambah Imunisasi"
    }
}