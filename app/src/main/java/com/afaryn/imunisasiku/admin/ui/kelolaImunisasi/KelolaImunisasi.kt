package com.afaryn.imunisasiku.admin.ui.kelolaImunisasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afaryn.imunisasiku.databinding.ActivityKelolaImunisasiBinding

@Suppress("DEPRECATION")
class KelolaImunisasi : AppCompatActivity() {

    private lateinit var binding:ActivityKelolaImunisasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelolaImunisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabTambahImunisasi.setOnClickListener{moveToTambahImunisasi()}
        binding.include.btnBack.setOnClickListener{onBackPressed()}
        ToolbarName()
    }

    private fun moveToTambahImunisasi(){
        val intent = Intent(this, TambahImunisasi::class.java)
        startActivity(intent)
    }

    private fun ToolbarName(){
        binding.include.tvToolbar.text="Kelola Imunisasi"
    }
}