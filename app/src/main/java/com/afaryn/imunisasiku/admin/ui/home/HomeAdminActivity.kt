package com.afaryn.imunisasiku.admin.ui.home


import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.jadwal.TambahJadwal
import com.afaryn.imunisasiku.admin.ui.profile.ProfileFragmentAdmin
import com.afaryn.imunisasiku.databinding.HomeAdminBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class HomeAdminActivity : AppCompatActivity() {
    private lateinit var binding: HomeAdminBinding
    private lateinit var navController: NavController

    private var isExpanded = false

    private val fromBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom_fab)
    }
    private val toBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom_fab)
    }
    private val rotateClockWiseFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate_clock_wise)
    }
    private val rotateAntiClockWiseFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate_anti_clock_wise)
    }
    private val fromBottomBgAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim)
    }
    private val toBottomBgAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim)
    }
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
//            btnAdd.setOnClickListener {
//                val intent = Intent(this@HomeAdminActivity,TambahJadwal::class.java)
//                startActivity(intent)
//            }
            btnAdd.setOnClickListener {

                if (isExpanded) {
                    shrinkFab()
                } else {
                    expandFab()
                }

            }
            btnHari.setOnClickListener {
                Toast.makeText(this@HomeAdminActivity,"btn Hari Clicked",Toast.LENGTH_SHORT).show()
            }
            btnJam.setOnClickListener {
                Toast.makeText(this@HomeAdminActivity,"btn Jam Clicked",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_layout,fragment)
        fragmentTransaction.commit()
    }

    private fun shrinkFab() {

        binding.transparentBg.startAnimation(toBottomBgAnim)
        binding.btnAdd.startAnimation(rotateAntiClockWiseFabAnim)
        binding.btnHari.startAnimation(toBottomFabAnim)
        binding.btnJam.startAnimation(toBottomFabAnim)
        binding.tvHari.startAnimation(toBottomFabAnim)
        binding.shareTv.startAnimation(toBottomFabAnim)



        isExpanded = !isExpanded
    }

    private fun expandFab() {

        binding.transparentBg.startAnimation(fromBottomBgAnim)


        binding.btnAdd.startAnimation(rotateClockWiseFabAnim)
        binding.btnHari.startAnimation(fromBottomFabAnim)
        binding.btnJam.startAnimation(fromBottomFabAnim)
        binding.tvHari.startAnimation(fromBottomFabAnim)
        binding.shareTv.startAnimation(fromBottomFabAnim)


        isExpanded = !isExpanded
    }

    override fun onBackPressed() {

        if (isExpanded) {
            shrinkFab()
        } else {
            super.onBackPressed()

        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        if (ev?.action == MotionEvent.ACTION_DOWN) {

            if (isExpanded) {
                val outRect = Rect()
                binding.fabConstraint.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    shrinkFab()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }


}