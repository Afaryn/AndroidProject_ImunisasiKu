package com.afaryn.sipeni.admin.ui.jadwal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.afaryn.sipeni.R
import com.afaryn.sipeni.admin.ui.jadwal.viewModel.jadwalViewModel
import com.afaryn.sipeni.databinding.ActivityJadwalImunisasiAdminBinding
import com.afaryn.sipeni.model.Imunisasi
import com.afaryn.sipeni.presentation.jadwalku.adapter.JadwalKuAdapter
import com.afaryn.sipeni.utils.UiState
import com.afaryn.sipeni.utils.hide
import com.afaryn.sipeni.utils.setupDeleteDialog
import com.afaryn.sipeni.utils.show
import com.afaryn.sipeni.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JadwalImunisasiAdminActivity : AppCompatActivity() {

    private var _binding: ActivityJadwalImunisasiAdminBinding? = null
    private val binding get() = _binding!!
    private val jadwalAdapter by lazy { JadwalKuAdapter() }
    private val viewModel by viewModels<jadwalViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityJadwalImunisasiAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getJadwal()

        binding.btnBack.setOnClickListener { finish() }
        binding.rvJadwalku.apply {
            adapter = jadwalAdapter
            layoutManager = LinearLayoutManager(this@JadwalImunisasiAdminActivity)
        }
        observer()
    }

    private fun observer() {
        viewModel.getImnState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    it.data?.let { imunisasiList -> setUpRv(imunisasiList) }
                }
                is UiState.Error -> {
                    toast(it.error!!)
                }
            }
        }

        viewModel.cancelImunisasiState.observe(this) {
            when (it) {
                is UiState.Success -> {
                    viewModel.getJadwal()
                    toast(it.data!!)
                }
                is UiState.Error -> {
                    toast(it.error!!)
                }
                else -> {}
            }
        }
    }

    private fun setUpRv(imunisasiList: List<Imunisasi>) {
        jadwalAdapter.differ.submitList(imunisasiList)
        jadwalAdapter.onDeleteClick = {
            setupDeleteDialog(
                title = "Batalkan imunisasi ${it.namaImunisasi}?",
                message = "Tekan batalkan jika anda ingin membatalkan imunisasi",
                btnActionText = getString(R.string.batalkan)
            ) {
                val imunisasiCancelled = it.copy(
                    statusImunisasi = "Dibatalkan Admin"
                )
                viewModel.cancelImunisasi(imunisasiCancelled)
                WorkManager.getInstance(this).cancelAllWorkByTag(it.id)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}