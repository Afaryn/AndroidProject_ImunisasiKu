package com.afaryn.imunisasiku.presentation.imunisasi

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.ActivityDaftarImunisasiBinding
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DaftarImunisasiActivity : AppCompatActivity() {

    private var _binding: ActivityDaftarImunisasiBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ImunisasiViewModel>()
    private var listJenisImunisasi: List<JenisImunisasi> = listOf()
    private var selectedImunisasi: JenisImunisasi = JenisImunisasi()
    private var selectedJadwal: String? = null
    private var selectedJam: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDaftarImunisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
        observer()
    }

    private fun setActions() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            btnSimpan.setOnClickListener {
                if (selectedJadwal.isNullOrEmpty()) {
                    toast("Pilih jadwal imunisasi yang sesuai")
                    return@setOnClickListener
                }

                if (selectedJadwal!!.contains("Tidak ada jadwal")) {
                    toast("Jadwal untuk imunisasi yang dipilih tidak tersedia")
                    return@setOnClickListener
                }

                toast(selectedJadwal!!)
            }
        }
    }

    private fun observer() {
        viewModel.imunisasiListState.observe(this) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    it.data?.let { jenisImunisasi ->
                        listJenisImunisasi = jenisImunisasi
                        setupSpinner(jenisImunisasi)
                    }
                }
                is UiState.Error -> {
                    toast(it.error ?: "Terjadi Kesalahan")
                }
            }
        }
    }

    private fun setupSpinner(jenisImunisasi: List<JenisImunisasi>) {
        val namaJenisImunisasi = mutableListOf<String>()
        val jadwalImunisasi = mutableListOf<String>()
        val jamImunisasi = mutableListOf<String>()

        jenisImunisasi.forEach {
            namaJenisImunisasi.add(it.namaImunisasi ?: "")
        }
        val jenisImunisasiAdapter = ArrayAdapter(this, R.layout.item_spinner, namaJenisImunisasi)
        val waktuImunisasiAdapter = ArrayAdapter(this, R.layout.item_spinner, jadwalImunisasi)
//        val jamImunisasiAdapter = ArrayAdapter(this, R.layout.item_spinner, jamImunisasi)

        binding.apply {
            acJenisImunisasi.setAdapter(jenisImunisasiAdapter)
            acTanggalImunisasi.setAdapter(waktuImunisasiAdapter)

            acJenisImunisasi.setOnItemClickListener { _, _, position, _ ->
                selectedJadwal = ""
                selectedImunisasi = jenisImunisasi[position]

                jadwalImunisasi.clear()
                if (selectedImunisasi.jadwalImunisasi.isNullOrEmpty()) {
                    jadwalImunisasi.add("Tidak ada jadwal untuk imunisasi ${selectedImunisasi.namaImunisasi} saat ini")
                } else {
                    selectedImunisasi.jadwalImunisasi?.forEach {
                        jadwalImunisasi.add(it)
                    }
                }

                acJamImunisasi.setText(selectedImunisasi.jamImunisasi)
            }

            acTanggalImunisasi.setOnItemClickListener { parent, _, position, _ ->
                selectedJadwal = parent.getItemAtPosition(position).toString()
            }

            acJamImunisasi.setOnItemClickListener { parent, _, position, _ ->
                selectedJam = parent.getItemAtPosition(position).toString()
                toast(selectedJam ?: "Null")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}