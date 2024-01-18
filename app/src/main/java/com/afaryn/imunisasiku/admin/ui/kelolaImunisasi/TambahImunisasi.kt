package com.afaryn.imunisasiku.admin.ui.kelolaImunisasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.afaryn.imunisasiku.admin.ui.home.HomeAdminActivity
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.viewModel.TambahImnViewModel
import com.afaryn.imunisasiku.databinding.ActivityTambahImunisasiBinding
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import dagger.hilt.android.AndroidEntryPoint


@Suppress("DEPRECATION")
@AndroidEntryPoint
class TambahImunisasi : AppCompatActivity() {

    private val viewModel by viewModels<TambahImnViewModel>()
    private var _binding: ActivityTambahImunisasiBinding?=null
    private val binding get()=_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityTambahImunisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ToolbarName()
        setAction()
        observer()
    }

    private fun setAction(){
        with(binding){
            include.btnBack.setOnClickListener{
                onBackPressed()
            }
            button2.setOnClickListener{
                if(!validateFields()){
                    Toast.makeText(this@TambahImunisasi,"Harap isi semua kolom",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val nama = tvNamaImunisasi.text.toString().trim()
                val usia = (tvUsiaImunisasi.text.toString().trim()).toInt()
                val jadwalHari = arrayListOf<String>()?:null
                if (cbSenin.isChecked){
                    jadwalHari!!.add("Senin")
                }
                if(cbSelasa.isChecked){
                    jadwalHari!!.add("Selasa")
                }
                if(cbRabu.isChecked){
                    jadwalHari!!.add("Rabu")
                }
                if(cbKamis.isChecked){
                    jadwalHari!!.add("Kamis")
                }
                if(cbJumat.isChecked){
                    jadwalHari!!.add("Jum'at")
                }
                if (cbSabtu.isChecked){
                    jadwalHari!!.add("Sabtu")
                }

                val jamMulai = edtJamMulai.text.toString()
                val jamSelesai = edtJamSelesai.text.toString()

                val jamImunisasi = "$jamMulai - $jamSelesai"

                val tambahImn = JenisImunisasi(
                    namaImunisasi = nama,
                    batasUmur = usia,
                    jadwalImunisasi = jadwalHari,
                    jamImunisasi = jamImunisasi
                )
                viewModel.sendImunisasi(tambahImn)
            }

        }
    }

    private fun observer(){
        viewModel.sendingState.observe(this){
            when(it){
                is UiState.Loading->{
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    Toast.makeText(this,it.data!!,Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,HomeAdminActivity::class.java)
                    startActivity(intent)
                }
                is UiState.Error -> {
                    Toast.makeText(this,it.error.toString()!!,Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun ToolbarName(){
        binding.include.tvToolbar.text="Tambah Imunisasi"
    }

    private fun validateFields(): Boolean {
        with(binding) {
            return tvNamaImunisasi.text!!.isNotEmpty() && tvUsiaImunisasi.text!!.isNotEmpty()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}