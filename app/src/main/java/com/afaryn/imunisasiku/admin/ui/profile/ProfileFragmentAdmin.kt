package com.afaryn.imunisasiku.admin.ui.profile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.profile.viewModel.profileViewModel
import com.afaryn.imunisasiku.databinding.FragmentHomeAdminBinding
import com.afaryn.imunisasiku.databinding.FragmentProfileAdminBinding
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragmentAdmin : Fragment() {

    private lateinit var binding:FragmentProfileAdminBinding
    private val viewModel by viewModels<profileViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileAdminBinding.inflate(inflater, container, false)
        val view = binding.root

        setPage()
        observe()
        setAction()

        return view
    }

    private fun setAction() {
        binding.apply {
            btnLogout.setOnClickListener {
                showCustomDialogBox("Apakah yakin akan keluar dari akun?")
//                    logout() }
            }
            btnEditProfile.setOnClickListener {
                val intent = Intent(context, EditProfile::class.java)
                startActivity(intent)
            }
            btnHapusAkun.setOnClickListener {
                showCustomDialogBoxHps("Apakah yakin ingin menghapus akun anda? ")
            }
        }
    }

    private fun showCustomDialogBox(message: String?) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val tvMessage: TextView = dialog.findViewById(R.id.tvMessage)
        val btnYes: Button = dialog.findViewById(R.id.btnYes)
        val btnNo: Button = dialog.findViewById(R.id.btnNo)

        tvMessage.text = message

        btnYes.setOnClickListener {
            Toast.makeText(requireContext(), "click on Yes", Toast.LENGTH_LONG).show()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showCustomDialogBoxHps(message: String?) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val tvMessage: TextView = dialog.findViewById(R.id.tvMessage)
        val btnYes: Button = dialog.findViewById(R.id.btnYes)
        val btnNo: Button = dialog.findViewById(R.id.btnNo)

        tvMessage.text = message

        btnYes.setOnClickListener {
            Toast.makeText(requireContext(), "click on Yes", Toast.LENGTH_LONG).show()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun setPage(){
        viewModel.getData()
    }

    private fun observe(){
        viewModel.getState.observe(viewLifecycleOwner){
            when(it){
                is UiState.Loading->{
                    // NOTE LOADING PAGE BELUM BISA MUNCUL (hlmn home sm profile)
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success->{
                    binding.tvNameProfile.text=it.data!!.name
                    binding.tvEmailProfile.text=it.data!!.email
                }
                is UiState.Error->{
                    Toast.makeText(context,it.error.toString(),Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    companion object {

    }
}