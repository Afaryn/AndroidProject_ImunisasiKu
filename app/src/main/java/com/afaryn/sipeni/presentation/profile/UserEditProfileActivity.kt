package com.afaryn.sipeni.presentation.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.sipeni.databinding.ActivityUserEditProfileBinding
import com.afaryn.sipeni.model.User
import com.afaryn.sipeni.presentation.onboarding.OnboardingActivity
import com.afaryn.sipeni.presentation.profile.viewmodel.EditProfileViewModel
import com.afaryn.sipeni.utils.UiState
import com.afaryn.sipeni.utils.hide
import com.afaryn.sipeni.utils.setupDeleteDialog
import com.afaryn.sipeni.utils.show
import com.afaryn.sipeni.utils.toast
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
                    deleteSharedPreference()
                    startActivity(
                        Intent(this, OnboardingActivity::class.java).apply {
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
                deleteSharedPreference()
                startActivity(
                    Intent(this@UserEditProfileActivity, OnboardingActivity::class.java).apply {
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

    private fun deleteSharedPreference() {
        val sharedPreferences = getSharedPreferences("UserRole", MODE_PRIVATE)
        sharedPreferences.edit().remove("role").apply()
        val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        sharedPref.edit().remove("Finished").apply()
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