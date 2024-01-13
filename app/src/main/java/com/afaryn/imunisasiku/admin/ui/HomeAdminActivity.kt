package com.afaryn.imunisasiku.admin.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afaryn.imunisasiku.databinding.HomeAdminBinding

class HomeAdminActivity : AppCompatActivity() {
    private lateinit var binding: HomeAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= HomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.CvImunisasi.setOnClickListener{moveToImunization()}
    }

    private fun moveToImunization(){
        val intent = Intent(this,KelolaImunisasi::class.java)
        startActivity(intent)
    }
}