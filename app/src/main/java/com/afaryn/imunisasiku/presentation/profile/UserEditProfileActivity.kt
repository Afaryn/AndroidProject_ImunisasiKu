package com.afaryn.imunisasiku.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.imunisasiku.databinding.ActivityUserEditProfileBinding
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.presentation.auth.AuthActivity
import com.afaryn.imunisasiku.presentation.profile.viewmodel.EditProfileViewModel
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.setupDeleteDialog
import com.afaryn.imunisasiku.utils.show
import com.afaryn.imunisasiku.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserEditProfileActivity : AppCompatActivity() {

    private var _binding: ActivityUserEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<EditProfileViewModel>()
    private var signedInUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observer()
        setActions()
    }

    private fun observer() {
        viewModel.userInfoState.observe(this) {
            when (it) {
                is UiState.Success -> {
                    it.data?.let { user -> setUpEditPage(user) }
                }
                is UiState.Error -> {
                    toast(it.error!!)
                }
                else -> {}
            }
        }

        viewModel.editProfileState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    toast(it.data!!)
                    finish()
                }
                is UiState.Error -> {
                    toast(it.error!!)
                }
            }
        }

        viewModel.deleteAccountState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    toast(it.data!!)
                    startActivity(
                        Intent(this, AuthActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    )
                }
                is UiState.Error -> {
                    toast(it.error!!)
                }
            }
        }
    }

    private fun setUpEditPage(user: User) {
        signedInUser = user
        binding.apply {
            rgJenisKelamin.check(
                if (user.jenisKelamin == "Laki - laki") radioLakiLaki.id
                else radioPerempuan.id
            )
            etNama.setText(user.name)
            etNik.setText(user.nik)
            etEmail.setText(user.email)
            etPhone.setText(user.phone)
        }
    }

    private fun setActions() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            tvKeluarAkun.setOnClickListener {
                viewModel.signOut()
                startActivity(
                    Intent(this@UserEditProfileActivity, AuthActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }

            tvHapusAkun.setOnClickListener {
                setupDeleteDialog(
                    title = "Yakin Hapus Akun?",
                    message = "Akun anda akan dihapus secara permanen",
                    btnActionText = "Hapus"
                ) {
                    viewModel.deleteUserData()
                }
            }

            btnSimpan.setOnClickListener {
                if (!validateFields()) {
                    toast("Harap isi semua kolom")
                    return@setOnClickListener
                } else if (signedInUser == null) {
                    toast("Terjadi masalah saat mencoba tersambung ke server, silahkan coba lagi nanti")
                    return@setOnClickListener
                }
                val selectedRadio = rgJenisKelamin.checkedRadioButtonId
                val namaLengkap = etNama.text.toString().trim()
                val jenisKelamin = findViewById<RadioButton>(selectedRadio).text.toString()
                val phone = etPhone.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val nik = etNik.text.toString().trim()

                val user = signedInUser!!.copy(
                    name = namaLengkap,
                    jenisKelamin = jenisKelamin,
                    phone = phone,
                    email = email,
                    nik = nik
                )

                viewModel.editProfile(user)
            }
        }
    }

    private fun validateFields(): Boolean {
        with(binding) {
            return rgJenisKelamin.checkedRadioButtonId != -1 && etNama.text.isNotEmpty() &&
                    etPhone.text.isNotEmpty() && etEmail.text.isNotEmpty()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}