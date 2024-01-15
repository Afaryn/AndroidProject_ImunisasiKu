package com.afaryn.imunisasiku.admin.ui.kelolaImunisasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.viewModel.TambahImnViewModel
import com.afaryn.imunisasiku.databinding.ActivityTambahImunisasiBinding
import com.afaryn.imunisasiku.model.Imunisasi
import com.afaryn.imunisasiku.model.JenisImunisasi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class TambahImunisasi : AppCompatActivity() {

    @Inject
    lateinit var viewModel: TambahImnViewModel

    private lateinit var binding:ActivityTambahImunisasiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTambahImunisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ToolbarName()
        binding.include.btnBack.setOnClickListener{onBackPressed()}
        binding.button2.setOnClickListener{
            val imunisasi = getImunisasiFromInput()
            viewModel.sendImunisasi(imunisasi)
            val loading = viewModel.isLoading.value.toString()

            Toast.makeText(this,loading,Toast.LENGTH_SHORT).show()
        }
    }

    private fun ToolbarName(){
        binding.include.tvToolbar.text="Tambah Imunisasi"
    }

    private fun getImunisasiFromInput(): JenisImunisasi{

        val nama = binding.tvNamaImunisasi.text.toString()
        val usia = (binding.tvUsiaImunisasi.text.toString()).toInt()
        val jadwalHari = arrayListOf<String>()

        if (binding.cbSenin.isChecked){
            jadwalHari.add("Senin")
        }
        if(binding.cbSelasa.isChecked){
            jadwalHari.add("Selasa")
        }
        if(binding.cbRabu.isChecked){
            jadwalHari.add("Rabu")
        }
        if(binding.cbKamis.isChecked){
            jadwalHari.add("Kamis")
        }
        if(binding.cbJumat.isChecked){
            jadwalHari.add("Jum'at")
        }
        if (binding.cbSabtu.isChecked){
            jadwalHari.add("Sabtu")
        }

        val jamMulai = binding.edtJamMulai.text.toString()
        val jamSelesai = binding.edtJamSelesai.text.toString()

        val jamImunisasi = "$jamMulai - $jamSelesai"



        return JenisImunisasi(namaImunisasi = nama, batasUmur = usia, jadwalImunisasi = jadwalHari,jamImunisasi=jamImunisasi)
    }
}