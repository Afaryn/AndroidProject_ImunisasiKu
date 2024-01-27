package com.afaryn.imunisasiku.presentation.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.afaryn.imunisasiku.MainActivity
import com.afaryn.imunisasiku.admin.ui.home.HomeAdminActivity
import com.afaryn.imunisasiku.databinding.ActivityAuthBinding
import com.afaryn.imunisasiku.presentation.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onStart() {
        super.onStart()

        if (viewModel.isUserLoggedIn()) {
            startActivity(Intent(this, getDestination()).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                finish()
            })
        }
    }

    private fun getDestination(): Class<*> {
        val sharedPref = getSharedPreferences("UserRole", Context.MODE_PRIVATE)
        val role = sharedPref.getString("role", "null")
        return if (role == "admin") HomeAdminActivity::class.java
        else MainActivity::class.java
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}