package com.afaryn.sipeni.admin.ui.home


import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.afaryn.sipeni.R
import com.afaryn.sipeni.admin.ui.jadwal.TambahHariActivity
import com.afaryn.sipeni.admin.ui.jadwal.TambahJamActivity
import com.afaryn.sipeni.admin.ui.profile.ProfileFragmentAdmin
import com.afaryn.sipeni.databinding.HomeAdminBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class HomeAdminActivity : AppCompatActivity() {
    private var _binding: HomeAdminBinding? = null
    private val binding get() = _binding!!
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
        _binding= HomeAdminBinding.inflate(layoutInflater)
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

                if (isExpanded) {
                    shrinkFab()
                } else {
                    expandFab()
                }

            }
            btnHari.setOnClickListener {
                val intent = Intent(this@HomeAdminActivity,TambahHariActivity::class.java)
                startActivity(intent)
            }
            btnJam.setOnClickListener {
                val intent = Intent(this@HomeAdminActivity, TambahJamActivity::class.java)
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

    private fun shrinkFab() {

        binding.transparentBg.startAnimation(toBottomBgAnim)
        binding.btnAdd.startAnimation(rotateAntiClockWiseFabAnim)
        binding.btnHari.startAnimation(toBottomFabAnim)
        binding.btnJam.startAnimation(toBottomFabAnim)
        binding.tvHari.startAnimation(toBottomFabAnim)
        binding.shareTv.startAnimation(toBottomFabAnim)
        binding.apply {
            btnHari.isClickable=false
            btnJam.isClickable=false
        }



        isExpanded = !isExpanded
    }

    private fun expandFab() {

        binding.transparentBg.startAnimation(fromBottomBgAnim)


        binding.btnAdd.startAnimation(rotateClockWiseFabAnim)
        binding.btnHari.startAnimation(fromBottomFabAnim)
        binding.btnJam.startAnimation(fromBottomFabAnim)
        binding.tvHari.startAnimation(fromBottomFabAnim)
        binding.shareTv.startAnimation(fromBottomFabAnim)

        binding.apply {
            btnHari.isClickable=true
            btnJam.isClickable=true
        }


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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}