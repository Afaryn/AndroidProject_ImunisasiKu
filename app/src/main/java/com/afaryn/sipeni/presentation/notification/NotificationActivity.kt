package com.afaryn.sipeni.presentation.notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.sipeni.databinding.ActivityNotificationBinding
import com.afaryn.sipeni.presentation.notification.adapter.NotificationAdapter
import com.afaryn.sipeni.presentation.notification.viewmodel.NotificationViewModel
import com.afaryn.sipeni.utils.setupDeleteDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {

    private var _binding: ActivityNotificationBinding? = null
    private val binding get() = _binding!!
    private val notificationAdapter by lazy { NotificationAdapter() }
    private val viewModel by viewModels<NotificationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvNotification.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(context)
        }
        observer()
        setActions()
    }

    private fun setActions() {
        binding.apply {
            btnBack.setOnClickListener { finish() }
            btnDeleteNotification.setOnClickListener {
                setupDeleteDialog(
                    title = "Hapus Semua Notifikasi?",
                    message = "Semua notifikasi akan dihapus",
                    btnActionText = "Ya"
                ) {
                    viewModel.deleteAllNotification()
                }
            }
        }
    }

    private fun observer() {
        viewModel.notificationState.asLiveData().observe(this) {
            notificationAdapter.differ.submitList(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}