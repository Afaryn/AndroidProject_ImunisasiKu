package com.afaryn.sipeni.admin.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import com.afaryn.sipeni.admin.ui.profile.viewModel.profileViewModel
import com.afaryn.sipeni.databinding.ActivityEditProfileBinding
import com.afaryn.sipeni.model.User
import com.afaryn.sipeni.utils.UiState
import com.afaryn.sipeni.utils.hide
import com.afaryn.sipeni.utils.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditProfile : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding
    private val viewModel by viewModels<profileViewModel>()
    private var oldData = User()
    private var jk = String()
    private val map = mutableMapOf<String,Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPage()
        observer()
        setAction()
    }

    @Suppress("DEPRECATION")
    private fun setAction(){
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                onBackPressed()
            }
            btnSimpan.setOnClickListener{
                if(!validateFields()){
                    Toast.makeText(this@EditProfile,"Harap isi semua kolom",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                binding.apply {
                    val selectedRadio = rgJenisKelamin.checkedRadioButtonId
                    jk = findViewById<RadioButton>(selectedRadio).text.toString()
                    map["jenisKelamin"]=jk
                    map["name"]=etNama.text.toString()
                    map["email"]=etEmail.text.toString()
                    map["phone"]=etPhone.text.toString()
                }
                viewModel.sendEditAkun(oldData,map)
            }
        }
    }

    private fun setPage(){
        viewModel.getData()
        binding.toolbar.tvToolbar.text="Edit Profile"
    }

    private fun observer(){
        viewModel.getState.observe(this){
            when(it){
                is UiState.Loading->{
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success->{
                    binding.progressBar.hide()
                    oldData = it.data!!
                    binding.apply {
                        etNama.setText(it.data!!.name)
                        etEmail.setText((it.data.email))
                        etPhone.setText((it.data.phone))
                        if (it.data.jenisKelamin=="Laki - laki"){
                            radioLakiLaki.isChecked=true
                        }
                        if(it.data.jenisKelamin=="Perempuan"){
                            radioPerempuan.isChecked=true
                        }
                    }
                }
                is UiState.Error -> {
                    binding.progressBar.hide()

                }
            }
        }
        viewModel.sendingState.observe(this){
            when(it){
                is UiState.Loading ->{
                    if(it.isLoading==true)binding.progressBar.show()
                    else  binding.progressBar.hide()
                }
                is UiState.Success->{
                    Toast.makeText(this,it.data!!, Toast.LENGTH_SHORT).show()
                    finish()
//                    val intent = Intent(this,KelolaAkun::class.java)
//                    startActivity(intent)
                }
                is UiState.Error->{
                    Toast.makeText(this,it.error!!, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        with(binding) {
            return etNama.text!!.isNotEmpty() && etEmail.text!!.isNotEmpty()
                    && etPhone.text.isNotEmpty()   && rgJenisKelamin.checkedRadioButtonId != -1
        }
    }
}