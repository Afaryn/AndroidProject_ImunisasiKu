package com.afaryn.sipeni.presentation.pasien

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.sipeni.R
import com.afaryn.sipeni.databinding.ActivityPasienBinding
import com.afaryn.sipeni.presentation.imunisasi.DaftarImunisasiActivity
import com.afaryn.sipeni.presentation.pasien.adapter.PasienAdapter
import com.afaryn.sipeni.presentation.pasien.viewmodel.PasienViewModel
import com.afaryn.sipeni.utils.Constants.PICK_PASIEN
import com.afaryn.sipeni.utils.Constants.REQUEST_PICK_PASIEN
import com.afaryn.sipeni.utils.UiState
import com.afaryn.sipeni.utils.hide
import com.afaryn.sipeni.utils.setupDeleteDialog
import com.afaryn.sipeni.utils.show
import com.afaryn.sipeni.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasienActivity : AppCompatActivity() {

    private var _binding: ActivityPasienBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PasienViewModel>()
    private lateinit var pasienAdapter: PasienAdapter
    private var pickPasien: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPasienBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getAllPasien()

        isPickPasien()
        setUpRecyclerView()
        setActions()
        observer()
    }

    @SuppressLint("SetTextI18n")
    private fun isPickPasien() {
        pickPasien = intent.getBooleanExtra(PICK_PASIEN, false)
        pasienAdapter = if (pickPasien) {
            binding.tvTitle.text = "Pilih Pasien"
            PasienAdapter(true)
        } else {
            PasienAdapter()
        }
    }

    private fun setUpRecyclerView() {
        binding.rvPasien.apply {
            adapter = pasienAdapter
            layoutManager = LinearLayoutManager(this@PasienActivity)
        }

        pasienAdapter.onPickPasienClick = {
            val intent = Intent(this, DaftarImunisasiActivity::class.java)
            intent.putExtra(PASIEN_PICKED, it)
            setResult(REQUEST_PICK_PASIEN, intent)
            finish()
        }

        pasienAdapter.onEditPasienClick = {
            val intent = Intent(this, TambahPasienActivity::class.java).apply {
                putExtra(TambahPasienActivity.PASIEN_INTENT, it)
            }
            startActivity(intent)
        }

        pasienAdapter.onDeletePasienClick = {
            setupDeleteDialog(
                title = "Hapus Data Pasien ${it.name}?",
                message = "Data pasien akan dihapus secara permanen",
                btnActionText = getString(R.string.hapus)
            ) {
                viewModel.deletePasien(it.id)
            }
        }
    }

    private fun setActions() {
        binding.btnBack.setOnClickListener {
            finish()
        }
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
        viewModel.deletePatientState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    toast(it.data!!)
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

    companion object {
        const val PASIEN_PICKED = "pasien_picked"
    }
}