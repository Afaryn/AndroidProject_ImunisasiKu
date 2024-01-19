@file:Suppress("DEPRECATION")

package com.afaryn.imunisasiku.admin.ui.kelolaAkun

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.adapter.KelAkunAdpater
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.viewModel.KelAkunViewModel
import com.afaryn.imunisasiku.databinding.ActivityEditAkunPenggunaBinding
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
                }
                is UiState.Error->{
                    Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun setRv(data:List<User>){
        myAdapter.differ.submitList(data)
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