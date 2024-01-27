package com.afaryn.imunisasiku.admin.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.ActivityBantuanBinding

class BantuanActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBantuanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBantuanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPage()
    }
    @Suppress("DEPRECATION")
    private fun setPage(){
        binding.include3.apply {
            tvToolbar.text="Bantuan"
            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

}