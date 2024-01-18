package com.afaryn.imunisasiku.admin.ui.home


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.profile.ProfileFragmentAdmin
import com.afaryn.imunisasiku.databinding.HomeAdminBinding

class HomeAdminActivity : AppCompatActivity() {
    private lateinit var binding: HomeAdminBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= HomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragmentAdmin())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_home -> replaceFragment(HomeFragmentAdmin())
                R.id.menu_profile -> replaceFragment(ProfileFragmentAdmin())
            }
            true

        }

    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_layout,fragment)
        fragmentTransaction.commit()
    }

}