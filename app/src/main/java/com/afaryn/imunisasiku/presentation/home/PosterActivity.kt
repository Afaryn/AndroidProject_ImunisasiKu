package com.afaryn.imunisasiku.presentation.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.ActivityPosterBinding
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show

class PosterActivity : AppCompatActivity() {

    private var _binding: ActivityPosterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.layoutPentingnyaImunisasi.setOnClickListener {
            startActivity(Intent(this, VideoActivity::class.java))
        }
        setUpPage()
    }

    private fun setUpPage() {
        when (intent.getStringExtra(POSTER_INTENT)) {
            AYO_IMUNISASI -> {
                binding.tvTitle.text = getString(R.string.ayo_imunisasi)
                binding.ivPoster.setImageResource(R.drawable.iv_ayo_imunisasi)
                binding.layoutPentingnyaImunisasi.show()
                binding.layoutBidanKami.hide()
            }
            JADWAL_IMUNISASI -> {
                binding.tvTitle.text = getString(R.string.jadwal_imunisasi)
                binding.ivPoster.setImageResource(R.drawable.iv_jadwal_imunisasi)
                binding.layoutPentingnyaImunisasi.hide()
                binding.layoutBidanKami.hide()
            }
            BIDAN_KAMI -> {
                binding.tvTitle.text = getString(R.string.bidan_kami)
                binding.layoutPentingnyaImunisasi.hide()
                binding.ivPoster.hide()
                binding.layoutBidanKami.show()
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
        const val BIDAN_KAMI = "bidan_kami"
    }
}