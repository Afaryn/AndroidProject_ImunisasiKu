package com.afaryn.imunisasiku.presentation.profile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.imunisasiku.databinding.ActivityRiwayatImunisasiBinding
import com.afaryn.imunisasiku.presentation.profile.adapter.RiwayatImunisasiAdapter
import com.afaryn.imunisasiku.presentation.profile.viewmodel.RiwayatImunisasiViewModel
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import com.afaryn.imunisasiku.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RiwayatImunisasiActivity : AppCompatActivity() {

    private var _binding: ActivityRiwayatImunisasiBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RiwayatImunisasiViewModel>()
    private val riwayatImunisasiAdapter by lazy { RiwayatImunisasiAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRiwayatImunisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvRiwayatImunisasi.apply {
            adapter = riwayatImunisasiAdapter
            layoutManager = LinearLayoutManager(context)
        }
        binding.btnBack.setOnClickListener { finish() }
        observer()
    }

    private fun observer() {
        viewModel.imunisasiState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    it.data?.let { imunisasiList -> riwayatImunisasiAdapter.differ.submitList(imunisasiList) }
                }
                is UiState.Error -> {
                    toast(it.error!!)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}