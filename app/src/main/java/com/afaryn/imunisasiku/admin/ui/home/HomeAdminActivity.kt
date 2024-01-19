package com.afaryn.imunisasiku.admin.ui.home


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.jadwal.TambahJadwal
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

        setAction()

    }

    private fun setAction(){
        binding.apply {
            bottomNavigationView.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.menu_home -> replaceFragment(HomeFragmentAdmin())
                    R.id.menu_profile -> replaceFragment(ProfileFragmentAdmin())
                }
                true
            }
            btnAdd.setOnClickListener {
                val intent = Intent(this@HomeAdminActivity,TambahJadwal::class.java)
                startActivity(intent)
            }
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_layout,fragment)
        fragmentTransaction.commit()
    }



}