package com.afaryn.imunisasiku.presentation.imunisasi

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.afaryn.imunisasiku.MainActivity
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.ActivityImunisasiKuBinding
import com.afaryn.imunisasiku.model.Imunisasi
import com.afaryn.imunisasiku.presentation.imunisasi.adapter.ImunisasiKuAdapter
import com.afaryn.imunisasiku.presentation.imunisasi.viewmodel.ImunisasiKuViewModel
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.setupDeleteDialog
import com.afaryn.imunisasiku.utils.show
import com.afaryn.imunisasiku.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImunisasiKuActivity : AppCompatActivity() {

    private var _binding: ActivityImunisasiKuBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ImunisasiKuViewModel>()
    private val imunisasiAdapter by lazy { ImunisasiKuAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityImunisasiKuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvImunisasiku.apply {
            adapter = imunisasiAdapter
            layoutManager = LinearLayoutManager(this@ImunisasiKuActivity)
        }
        binding.btnBack.setOnClickListener { finish() }
        observer()
    }

    private fun observer() {
        viewModel.imunisasiKuState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    it.data?.let { imunisasi ->
                        setRvData(imunisasi)
                    }
                }
                is UiState.Error -> {
                    toast(it.error ?: "Terjadi Kesalahan")
                }
            }
        }

        viewModel.cancelImunisasiState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    toast(it.data!!)
                    val intent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
                is UiState.Error -> {
                    toast(it.error ?: "Terjadi Kesalahan")
                }
            }
        }
    }

    private fun setRvData(imunisasi: List<Imunisasi>) {
        imunisasiAdapter.differ.submitList(imunisasi)
        imunisasiAdapter.onDeleteClick = {
            setupDeleteDialog(
                title = "Batalkan imunisasi ${it.namaImunisasi}?",
                message = "Tekan batalkan jika anda ingin membatalkan imunisasi",
                btnActionText = getString(R.string.batalkan)
            ) {
                viewModel.batalkanImunisasi(it.id)
                WorkManager.getInstance(this).cancelAllWorkByTag(it.id)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}