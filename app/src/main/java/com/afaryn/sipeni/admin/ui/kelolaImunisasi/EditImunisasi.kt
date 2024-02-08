package com.afaryn.sipeni.admin.ui.kelolaImunisasi

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.sipeni.R
import com.afaryn.sipeni.admin.ui.kelolaImunisasi.viewModel.TambahImnViewModel
import com.afaryn.sipeni.databinding.ActivityEditImunisasiBinding
import com.afaryn.sipeni.model.JenisImunisasi
import com.afaryn.sipeni.utils.UiState
import com.afaryn.sipeni.utils.hide
import com.afaryn.sipeni.utils.parseDateString
import com.afaryn.sipeni.utils.show
import com.afaryn.sipeni.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class EditImunisasi : AppCompatActivity() {

    private lateinit var binding: ActivityEditImunisasiBinding
    private var jenisImunisasi = JenisImunisasi()
    private var imunisasiBackup = JenisImunisasi()
    private val viewModel by viewModels<TambahImnViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditImunisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPage()
        getData()
        setActions()
        observer()
    }

    private fun observer() {
        viewModel.editState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    toast(it.data!!)
                    finish()
                }
                is UiState.Error -> {
                    toast(it.error ?: "Terjadi Kesalahan")
                }
            }
        }
    }

    private fun setActions() {
        binding.button2.setOnClickListener {
            val nama = binding.tvNamaImunisasi.text.toString().trim()
            val usia = binding.etUsiaImunisasi.text.toString().trim()

            if (nama.isEmpty() || usia.isEmpty()) {
                toast("Harap isi nama dan usia imunisasi")
                return@setOnClickListener
            }

            jenisImunisasi = jenisImunisasi.copy(
                namaImunisasi = nama,
                batasUmur = usia.toInt()
            )

            viewModel.editImunisasi(imunisasiBackup, jenisImunisasi)
        }
    }

    @SuppressLint("InflateParams")
    private fun getData() {
        val dataImunisasi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EDIT_IMUNISASI, JenisImunisasi::class.java)
        } else {
            intent.getParcelableExtra(EDIT_IMUNISASI)
        }
        dataImunisasi?.let { imunisasi ->
            jenisImunisasi = imunisasi
            imunisasiBackup = imunisasi
            binding.apply {
                tvNamaImunisasi.setText(imunisasi.namaImunisasi)
                etUsiaImunisasi.setText(imunisasi.batasUmur.toString())

                handleJadwalDanJam(imunisasi)
            }
        }
    }

    private fun handleJadwalDanJam(it: JenisImunisasi) {
        binding.apply {
            it.jadwalImunisasi?.let { list ->
                for (jadwal in list) {
                    val inflater = LayoutInflater.from(applicationContext)
                        .inflate(R.layout.item_imunisasi_edit, null)
                    jadwalLayout.addView(inflater, jadwalLayout.childCount)
                }

                val count = jadwalLayout.childCount
                for (c in 0 until count) {
                    val v = jadwalLayout.getChildAt(c)
                    val tvContent = v.findViewById<TextView>(R.id.tv_content)
                    val btnDeleteEdit = v.findViewById<ImageView>(R.id.btn_delete_edit)

                    val currentListItem = list[c]
                    tvContent.text = parseDateString(list[c])
                    btnDeleteEdit.setOnClickListener {
                        v.hide()
                        jenisImunisasi.jadwalImunisasi?.remove(currentListItem)
                    }
                }
            }

            it.jamImunisasi?.let { listJam ->
                for (jam in listJam) {
                    val inflater = LayoutInflater.from(applicationContext)
                        .inflate(R.layout.item_imunisasi_edit, null)
                    jamLayout.addView(inflater, jamLayout.childCount)
                }

                val count = jamLayout.childCount
                for (c in 0 until count) {
                    val v = jamLayout.getChildAt(c)
                    val tvContent = v.findViewById<TextView>(R.id.tv_content)
                    val btnDeleteEdit = v.findViewById<ImageView>(R.id.btn_delete_edit)

                    val currentListItem = listJam[c]
                    tvContent.text = listJam[c]
                    btnDeleteEdit.setOnClickListener {
                        v.hide()
                        jenisImunisasi.jamImunisasi?.remove(currentListItem)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setPage() {
        with(binding) {
            include.tvToolbar.text = "Edit Data Imunisasi"
            include.btnBack.setOnClickListener { finish() }
        }
    }

    companion object {
        const val EDIT_IMUNISASI = "edit_imunisasi"
    }
}