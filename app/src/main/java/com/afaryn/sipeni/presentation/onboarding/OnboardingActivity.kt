package com.afaryn.sipeni.presentation.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.afaryn.sipeni.R
import com.afaryn.sipeni.databinding.ActivityOnboardingBinding
import com.afaryn.sipeni.presentation.auth.AuthActivity
import com.afaryn.sipeni.presentation.onboarding.adapter.OnBoardingItem
import com.afaryn.sipeni.presentation.onboarding.adapter.OnboardingAdapter

class OnboardingActivity : AppCompatActivity() {

    private var _binding: ActivityOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnboardingBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        setUpOnBoarding()
        binding.viewPager2.adapter = onboardingAdapter
        setUpOnBoarding()
        setUpBoardingIndicators()
        setActiveIndicators(0)
        setUpViewPager()
    }

    private fun setUpViewPager() {
        binding.viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setActiveIndicators(position)
            }
        })
        binding.btnNext.setOnClickListener {
            if (binding.viewPager2.currentItem + 1 < onboardingAdapter.itemCount) {
                binding.viewPager2.currentItem = binding.viewPager2.currentItem + 1
            } else {
                onBoardingFinished()
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        binding.btnSkip.setOnClickListener {
            onBoardingFinished()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setActiveIndicators(index: Int) {
        val childCount = binding.viewpagerIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = binding.viewpagerIndicator[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
        when (index) {
            0 -> {
                binding.btnNext.text = getString(R.string.next)
            }

            onboardingAdapter.itemCount - 1 -> {
                binding.btnNext.text = getString(R.string.mulai)
            }

            else -> {
                binding.btnNext.text = getString(R.string.next)
            }
        }
    }

    private fun setUpBoardingIndicators() {
        val indicators = arrayOfNulls<ImageView>(onboardingAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                24,
                24
            )
        layoutParams.setMargins(8,0,8,0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.viewpagerIndicator.addView(indicators[i])
        }
    }

    private fun setUpOnBoarding() {
        val onBoardingItems: MutableList<OnBoardingItem> = ArrayList()
        val onBoarding1 = OnBoardingItem(
            R.drawable.iv_onboarding_1,
            getString(R.string.onboarding_1),
            getString(R.string.onboarding_content_1)
        )
        val onBoarding2 = OnBoardingItem(
            R.drawable.iv_onboarding_2,
            getString(R.string.onboarding_2),
            getString(R.string.onboarding_content_2)
        )
        val onBoarding3 = OnBoardingItem(
            R.drawable.iv_onboarding_3,
            getString(R.string.onboarding_3),
            getString(R.string.onboarding_content_3)
        )
        onBoardingItems.add(onBoarding1)
        onBoardingItems.add(onBoarding2)
        onBoardingItems.add(onBoarding3)
        onboardingAdapter = OnboardingAdapter()
        onboardingAdapter.setNewItem(onBoardingItems)
    }

    private fun onBoardingFinished() {
        val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

    private fun isOnBoardingFinished(): Boolean {
        val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    override fun onStart() {
        super.onStart()
        if (isOnBoardingFinished()) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}