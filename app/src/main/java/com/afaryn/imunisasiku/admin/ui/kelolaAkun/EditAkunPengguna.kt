package com.afaryn.imunisasiku.admin.ui.kelolaAkun

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.home.HomeAdminActivity
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.viewModel.KelAkunViewModel
import com.afaryn.imunisasiku.databinding.ActivityEditAkunPenggunaBinding
import com.afaryn.imunisasiku.databinding.ActivityPasienBinding
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
@AndroidEntryPoint
class EditAkunPengguna : AppCompatActivity() {

    private var _binding: ActivityEditAkunPenggunaBinding? = null
    private val binding get() = _binding!!
    private var oldData = User()
    private lateinit var newRole:String
    private val viewModel by viewModels <KelAkunViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditAkunPenggunaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPage()
        setAction()
        observe()
    }

    @Suppress("DEPRECATION")
    private fun setAction(){
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                onBackPressed()
            }
            btnEditAkun.setOnClickListener{
                viewModel.sendEditAkun(oldData,newData())
            }
        }
    }

    private fun newData():Map<String,Any>{
        val map = mutableMapOf<String,Any>()
        val rg = binding.rgRolePengguna.checkedRadioButtonId
        when(rg){
            R.id.radio_Admin-> {
                newRole = "admin"
            }
            R.id.radio_NonAdmin->{
                newRole = "user"
            }
        }
        map["role"]=newRole
        return map
    }

    private fun setPage(){
        with(binding){
            toolbar.tvToolbar.text="Edit Akun Pengguna"
            getData()

            etNama.setText(oldData.name)
            etEmail.setText(oldData.email)
            etPhone.setText(oldData.phone)
            etJk.setText(oldData.jenisKelamin)

            if(oldData.role!=null){
                when(oldData.role){
                    "admin" ->{
                        radioAdmin.isChecked=true
                        radioNonAdmin.isChecked=false
                    }
                    "user"->{
                        radioAdmin.isChecked=false
                        radioNonAdmin.isChecked=true
                    }
                }
            }

            etNama.isFocusable=false
            etJk.isFocusable=false
            etEmail.isFocusable=false
            etPhone.isFocusable=false


        }
    }

    private fun getData(){
        val nama = intent.getStringExtra(USER_NAME)
        val email = intent.getStringExtra(USER_EMAIL)
        val jk = intent.getStringExtra(USER_JK)
        val phone = intent.getStringExtra(USER_PHONE)
        val role = intent.getStringExtra(USER_ROLE)

        oldData = User(name=nama,email=email, jenisKelamin = jk, phone = phone,role=role)
    }

    private fun observe(){
        viewModel.sendingState.observe(this){
            when(it){
                is UiState.Loading ->{
                    if(it.isLoading==true)binding.progressBar.show()
                    else  binding.progressBar.hide()
                }
                is UiState.Success->{
                    Toast.makeText(this,it.data!!,Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this, KelolaAkun::class.java).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    }
//                    startActivity(intent)
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

    companion object{
        const val USER_NAME = "user_nama"
        const val USER_EMAIL = "user_email"
        const val USER_JK = "user_jk"
        const val USER_PHONE = "user_phone"
        const val USER_ROLE = "user_role"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun finish() {
        super.finish()
        _binding = null
    }
}