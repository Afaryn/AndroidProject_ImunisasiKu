package com.afaryn.imunisasiku.admin.ui.jadwal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.ActivityTambahImunisasiBinding
import com.afaryn.imunisasiku.databinding.ActivityTambahJadwalBinding
import com.afaryn.imunisasiku.databinding.ListItemImunisasiBinding

class TambahJadwal : AppCompatActivity() {

    private var _binding :ActivityTambahJadwalBinding? = null
    private val binding get()= _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityTambahJadwalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCboxData()
        setupPage()
    }

    private fun getCboxData(){
        val items = listOf("nana","nini","nene","aa","bb","bb","bb","bb","bb","bb","bb","bb","bb","bb","bb","bb",)

        val autoCompleteTextView : AutoCompleteTextView = binding.autoComplete
        val adapter = ArrayAdapter(this,R.layout.list_item_imunisasi,items)

        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, i, l ->
            val itemSelected = adapterView.getItemAtPosition(i)
            Toast.makeText(this,"item selected :$itemSelected",Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("DEPRECATION")
    private fun setupPage(){
        binding.apply {
            include.tvToolbar.text = "Tambah Jadwal Imunisasi"
            include.btnBack.setOnClickListener { onBackPressed() }
        }
    }
}