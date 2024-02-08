package com.afaryn.sipeni.presentation.onboarding.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.sipeni.databinding.ViewPagerContainerBinding

class OnboardingAdapter: RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    private var onBoardingItems: MutableList<OnBoardingItem> = mutableListOf()

    inner class OnboardingViewHolder(val binding: ViewPagerContainerBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            ViewPagerContainerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = onBoardingItems.size

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        with(holder) {
            with(onBoardingItems[position]) {
                binding.tvOnboardingTitle.text = this.title
                binding.tvOnboardingContent.text = this.message
                binding.ivOnboarding.setImageResource(this.photo)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNewItem(data: List<OnBoardingItem>) {
        onBoardingItems.clear()
        onBoardingItems.addAll(data)
        notifyDataSetChanged()
    }

}

data class OnBoardingItem(
    val photo: Int,
    val title: String,
    val message: String
)