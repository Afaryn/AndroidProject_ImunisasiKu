@file:Suppress("DEPRECATION")

package com.afaryn.imunisasiku.admin.ui.kelolaAkun

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.home.HomeAdminActivity
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.adapter.KelAkunAdpater
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.viewModel.KelAkunViewModel
import com.afaryn.imunisasiku.databinding.ActivityKelolaAkunBinding
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KelolaAkun : AppCompatActivity() {

    private var _binding: ActivityKelolaAkunBinding? = null
    private val binding get() = _binding!!
    private val myAdapter by lazy{KelAkunAdpater()}
    private val viewModel by viewModels<KelAkunViewModel>()
    private val listData: MutableList<User> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivityKelolaAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar()
        setupRv()
        observer()
    }

    private fun toolbar(){
        binding.include.tvToolbar.text="Kelola Akun"
        binding.include.btnBack.setOnClickListener{onBackPressed()}
    }

    private fun observer(){
        viewModel.getDataState.observe(this){
            when(it){
                is UiState.Loading->{
                    if(it.isLoading==true) binding.progressBar.show()
                    else  binding.progressBar.hide()
                }
                is UiState.Success->{
                    setRv(it.data!!)
                    listData.addAll(it.data)
                }
                is UiState.Error->{
                    Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
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
    private fun setRv(data:List<User>){
        myAdapter.differ.submitList(data)
        myAdapter.onDeleteClick = {
            showCustomDialogBox("Apakah ingin menghapus Akun ${it.email}",it.email!!)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val listFiltered = listData.filter { imunisasi ->
                    imunisasi.name!!.contains(newText.orEmpty(), ignoreCase = true)
                }
                myAdapter.differ.submitList(listFiltered)
                return false
            }
        })
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

    private fun setupRv(){
        viewModel.getAllData()
        val layoutManager= LinearLayoutManager(this)
        binding.rvAkun.layoutManager=layoutManager
        binding.rvAkun.adapter = myAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}