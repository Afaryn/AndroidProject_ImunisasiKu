package com.afaryn.imunisasiku.presentation.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.afaryn.imunisasiku.MainActivity
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.home.HomeAdminActivity
import com.afaryn.imunisasiku.databinding.FragmentAdminLoginBinding
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
class AdminLoginFragment : Fragment() {

    private var _binding: FragmentAdminLoginBinding?=null
    private val binding get()=_binding!!
    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
        observer()
    }

    private fun setActions(){
        with(binding){
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnLogin.setOnClickListener{
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                viewModel.login(email,password)
            }
        }
    }

    private fun observer(){
        viewModel.loginState.observe(viewLifecycleOwner){
            when(it){
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {

                    startActivity(Intent(requireContext(), HomeAdminActivity::class.java).also { intent ->
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        requireActivity().finish()
                    })
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}