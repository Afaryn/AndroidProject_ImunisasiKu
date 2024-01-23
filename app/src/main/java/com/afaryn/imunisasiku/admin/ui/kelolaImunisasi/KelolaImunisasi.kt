package com.afaryn.imunisasiku.admin.ui.kelolaImunisasi

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.home.HomeAdminActivity
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.adapter.ImunisasiAdapter
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.viewModel.TambahImnViewModel
import com.afaryn.imunisasiku.databinding.ActivityKelolaImunisasiBinding
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
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
                    binding.progressBar.hide()
                    Toast.makeText(this, it.error.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.delState.observe(this){
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeAdminActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }

                is UiState.Error -> {
                    Toast.makeText(this, it.error.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRvData(data: List<JenisImunisasi>) {
        myAdapter.differ.submitList(data)
        myAdapter.onDeleteClick = {
            showCustomDialogBox("Apakah ingin menghapus Imunisasi ${it.namaImunisasi}",it.namaImunisasi!!)

        }
    }

    private fun showCustomDialogBox(message: String?,item: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val tvMessage: TextView = dialog.findViewById(R.id.tvMessage)
        val btnYes: Button = dialog.findViewById(R.id.btnYes)
        val btnNo: Button = dialog.findViewById(R.id.btnNo)

        tvMessage.text = message

        btnYes.setOnClickListener {
            viewModel.DelImunisasi(item)
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupRv() {
        viewModel.getAllData()
        val layoutManager = LinearLayoutManager(this)
        binding.rvImunisasi.layoutManager = layoutManager
        binding.rvImunisasi.adapter=myAdapter
    }

}