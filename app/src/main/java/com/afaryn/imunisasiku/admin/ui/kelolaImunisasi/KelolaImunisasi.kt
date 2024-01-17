package com.afaryn.imunisasiku.admin.ui.kelolaImunisasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.data.ImunisasiAdapter
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.viewModel.TambahImnViewModel
import com.afaryn.imunisasiku.databinding.ActivityKelolaImunisasiBinding
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.utils.Constants
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@Suppress("DEPRECATION")
class KelolaImunisasi : AppCompatActivity() {

    private lateinit var binding:ActivityKelolaImunisasiBinding

    private val viewModel by viewModels<TambahImnViewModel>()
    private val myAdapter by lazy { ImunisasiAdapter() }
    private lateinit var listData :ArrayList<JenisImunisasi>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelolaImunisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAction()
        ToolbarName()

//        tampilData()
            observer()
        setupRv()
    }

//    private fun tampilData(){
//        val rv = binding.rvImunisasi
//        rv.layoutManager = LinearLayoutManager(this)
//        rv.setHasFixedSize(true)
//
//        listData = arrayListOf()
//
//        adapter = ImunisasiAdapter(listData)
//
//        rv.adapter=adapter
//        getData()
//    }

    private fun setAction(){
        with(binding){
            fabTambahImunisasi.setOnClickListener{
                moveToTambahImunisasi()
            }
            include.btnBack.setOnClickListener{
                onBackPressed()
            }
        }
    }

    private fun moveToTambahImunisasi(){
        val intent = Intent(this, TambahImunisasi::class.java)
        startActivity(intent)
    }

    private fun ToolbarName(){
        binding.include.tvToolbar.text="Kelola Imunisasi"
    }

    private fun observer() {
        viewModel.getDataState.observe(this){
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {

                    setupRvData(it.data!!)
                }
                is UiState.Error -> {
                    Toast.makeText(this, it.error.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRvData(data: List<JenisImunisasi>) {
        myAdapter.differ.submitList(data)
    }

    private fun setupRv() {
        viewModel.getAllData()
        val layoutManager = LinearLayoutManager(this)
        binding.rvImunisasi.layoutManager = layoutManager
        binding.rvImunisasi.adapter=myAdapter
    }

}