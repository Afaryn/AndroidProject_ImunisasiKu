package com.afaryn.imunisasiku.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.afaryn.imunisasiku.databinding.FragmentRegisterBinding
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.presentation.auth.viewmodel.AuthViewModel
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.Validation
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import com.afaryn.imunisasiku.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
        observer()
    }

    private fun setActions() {
        with(binding) {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnRegister.setOnClickListener {
                if (!validateFields()) {
                    toast("Harap isi semua kolom")
                    return@setOnClickListener
                }
                val selectedRadio = rgJenisKelamin.checkedRadioButtonId
                val namaLengkap = etNama.text.toString().trim()
                val jenisKelamin = requireActivity().findViewById<RadioButton>(selectedRadio).text.toString()
                val phone = etPhone.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                val user = User(
                    name = namaLengkap,
                    email = email,
                    jenisKelamin = jenisKelamin,
                    phone = phone
                )

                viewModel.register(user, password, confirmPassword)
            }
        }
    }

    private fun observer() {
        viewModel.registerState.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    toast(it.data!!)
                    findNavController().navigateUp()
                }
                is UiState.Error -> {
                    toast(it.error.toString())
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validation.collect { validation ->
                    if (validation.email is Validation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.etEmail.apply {
                                requestFocus()
                                toast(validation.email.message)
                            }
                        }
                    }

                    if (validation.password is Validation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.etPassword.apply {
                                requestFocus()
                                toast(validation.password.message)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        with(binding) {
            return rgJenisKelamin.checkedRadioButtonId != -1 && etNama.text.isNotEmpty() &&
                    etPhone.text.isNotEmpty() && etEmail.text.isNotEmpty()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}