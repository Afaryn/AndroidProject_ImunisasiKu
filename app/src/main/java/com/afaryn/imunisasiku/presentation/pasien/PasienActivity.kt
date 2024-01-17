package com.afaryn.imunisasiku.presentation.pasien

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.imunisasiku.databinding.ActivityPasienBinding
import com.afaryn.imunisasiku.presentation.pasien.adapter.PasienAdapter
import com.afaryn.imunisasiku.presentation.pasien.viewmodel.PasienViewModel
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import com.afaryn.imunisasiku.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasienActivity : AppCompatActivity() {

    private var _binding: ActivityPasienBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PasienViewModel>()
    private val pasienAdapter by lazy { PasienAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPasienBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getAllPasien()

        setUpRecyclerView()
        setActions()
        observer()
    }

    private fun setUpRecyclerView() {
        binding.rvPasien.apply {
            adapter = pasienAdapter
            layoutManager = LinearLayoutManager(this@PasienActivity)
        }
    }

    private fun setActions() {
        binding.btnTambahPasien.setOnClickListener {
            startActivity(Intent(this, TambahPasienActivity::class.java))
        }
    }

    private fun observer() {
        viewModel.patientState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    it.data?.let { pasien ->
                        pasienAdapter.differ.submitList(pasien)
                    }
                }
                is UiState.Error -> {
                    toast(it.error ?: "Terjadi kesalahan")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}