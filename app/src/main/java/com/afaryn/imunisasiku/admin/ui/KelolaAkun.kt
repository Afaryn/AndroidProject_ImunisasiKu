@file:Suppress("DEPRECATION")

package com.afaryn.imunisasiku.admin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.ActivityKelolaAkunBinding

class KelolaAkun : AppCompatActivity() {

    private lateinit var binding:ActivityKelolaAkunBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityKelolaAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar()
    }

    private fun toolbar(){
        binding.include.tvToolbar.text="Kelola Akun"
        binding.include.btnBack.setOnClickListener{onBackPressed()}
    }
}