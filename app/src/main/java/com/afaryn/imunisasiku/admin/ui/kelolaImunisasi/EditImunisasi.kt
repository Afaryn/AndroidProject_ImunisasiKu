package com.afaryn.imunisasiku.admin.ui.kelolaImunisasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.ActivityEditImunisasiBinding

class EditImunisasi : AppCompatActivity() {

    private lateinit var binding:ActivityEditImunisasiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEditImunisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
        setPage()
    }

    private fun getData(){
        val nama_imunisasi = intent.getStringExtra(IMUNISASI)
        val batas_usia = intent.getStringExtra(BATAS_USIA)
        val jadwal_imunisasi = intent.getStringArrayListExtra(JADWAL_IMUNISASI)
        val jam_imunisasi = intent.getStringExtra(JAM_IMUNISASI)
        val splitJam = jam_imunisasi!!.split(" - ")

        binding.apply {
            tvNamaImunisasi.setText(nama_imunisasi)
            tvUsiaImunisasi.setText(batas_usia)
            if(jadwal_imunisasi!=null){
                for(i in 0..(jadwal_imunisasi.size-1)){
                    if(jadwal_imunisasi.get(i)=="Senin"){
                        cbSenin.isChecked = true
                    }
                    if(jadwal_imunisasi.get(i)=="Selasa"){
                        cbSelasa.isChecked = true
                    }
                    if(jadwal_imunisasi.get(i)=="Rabu"){
                        cbRabu.isChecked = true
                    }
                    if(jadwal_imunisasi.get(i)=="Kamis"){
                        cbKamis.isChecked =true
                    }
                    if(jadwal_imunisasi.get(i)=="Jum'at"){
                        cbJumat.isChecked = true
                    }
                    if(jadwal_imunisasi.get(i)=="Sabtu"){
                        cbSabtu.isChecked = true
                    }
                }
            }
            if (jam_imunisasi!=null){
                edtJamMulai.setText(splitJam[0])
                edtJamSelesai.setText(splitJam[1])
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun setPage(){
        with(binding){
            include.tvToolbar.text="Edit Data Imunisasi"
            include.btnBack.setOnClickListener { onBackPressed() }
        }
    }

    companion object{
        const val IMUNISASI = "nama_imunisasi"
        const val BATAS_USIA = "batas_usia"
        const val JADWAL_IMUNISASI="jadwal_imunisasi"
        const val JAM_IMUNISASI = "jam_imunisasi"
    }
}