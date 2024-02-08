package com.afaryn.sipeni.admin.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afaryn.sipeni.databinding.ActivitySyaratKetentuanBinding

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