package com.afaryn.imunisasiku.presentation.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.ActivityPosterBinding

class PosterActivity : AppCompatActivity() {

    private var _binding: ActivityPosterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        setUpPage()
    }

    private fun setUpPage() {
        when (intent.getStringExtra(POSTER_INTENT)) {
            AYO_IMUNISASI -> {
                binding.tvTitle.text = getString(R.string.ayo_imunisasi)
                binding.ivPoster.setImageResource(R.drawable.iv_ayo_imunisasi)
            }
            JADWAL_IMUNISASI -> {
                binding.tvTitle.text = getString(R.string.jadwal_imunisasi)
                binding.ivPoster.setImageResource(R.drawable.iv_jadwal_imunisasi)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val POSTER_INTENT = "poster_intent"
        const val AYO_IMUNISASI = "ayo_imunisasi"
        const val JADWAL_IMUNISASI = "jadwal_imunisasi"
    }
}