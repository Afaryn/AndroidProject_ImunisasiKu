package com.afaryn.imunisasiku.admin.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.ActivitySyaratKetentuanBinding

class SyaratKetentuanActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySyaratKetentuanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySyaratKetentuanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPage()
    }

    @Suppress("DEPRECATION")
    private fun setPage(){
        binding.include2.apply {
            tvToolbar.text="Syarat & Ketentuan"
            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
    }
}