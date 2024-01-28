package com.afaryn.imunisasiku.admin.ui.profile

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.profile.viewModel.profileViewModel
import com.afaryn.imunisasiku.databinding.FragmentProfileAdminBinding
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.presentation.onboarding.OnboardingActivity
import com.afaryn.imunisasiku.utils.Constants
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.reduceFileImage
import com.afaryn.imunisasiku.utils.show
import com.afaryn.imunisasiku.utils.toast
import com.afaryn.imunisasiku.utils.uriToFile
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragmentAdmin : Fragment() {

    private var _binding: FragmentProfileAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<profileViewModel>()
    private var permissionGiven: Boolean = false
    private var dataUser: User? = null

    private val permission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val isGranted = permissions[Manifest.permission.CAMERA] ?: false
            val readData = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
            val readMediaImages =
                permissions[if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                }] ?: false

            if (isGranted && readData) {
                permissionGiven = true
            } else if (readMediaImages) {
                permissionGiven = true
            }
        }

    private fun permissionSet() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
        } else {
            permission.launch(Constants.PERMISSIONS)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileAdminBinding.inflate(inflater, container, false)
        val view = binding.root

        setPage()
        observe()
        setAction()
        permissionSet()


        return view
    }

    private fun setAction() {
        binding.apply {
            binding.ivTakePicture.setOnClickListener {
                showCustomDialogBoxPhoto("Apakah ingin mengganti foto profile?")
            }

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
            btnSyaratKetentuan.setOnClickListener {
                val intent = Intent(context, SyaratKetentuanActivity::class.java)
                startActivity(intent)
            }
            btnBantuan.setOnClickListener {
                val intent = Intent(context, BantuanActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showCustomDialogBoxPhoto(message: String?) {
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
//            permissionGiven?.let {
//                val chooser = setUpGallery()
//                launchGallery.launch(chooser)
//                dialog.dismiss()
//            }
//            Toast.makeText(requireContext(), "Tidak dapat membuka Galery, Izin belum di berikan", Toast.LENGTH_LONG).show()
//                dialog.dismiss()
//            permissionSet()
            if (!permissionGiven) {
                Toast.makeText(requireContext(), "Tidak dapat membuka Galery, Izin belum di berikan", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
            val chooser = setUpGallery()
            launchGallery.launch(chooser)
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setUpGallery(): Intent {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        return Intent.createChooser(intent, "Choose a Picture")
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, requireContext())
                val setFile = reduceFileImage(myFile)
                val photoByteArray = setFile.readBytes()
                dataUser?.let { viewModel.gantiFotoProfil(it, photoByteArray) }
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
            viewModel.logout()
            deleteSharedPreference()
            startActivity(
                Intent(requireContext(), OnboardingActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteSharedPreference() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserRole", AppCompatActivity.MODE_PRIVATE)
        sharedPreferences.edit().remove("role").apply()
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        sharedPref.edit().remove("Finished").apply()
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
            viewModel.deleteUserData()
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
                    dataUser=it.data
                    if (!it.data!!.profile.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(it.data.profile)
                            .centerCrop()
                            .into(binding.imgItemPhoto)
                    }
                    binding.tvNameProfile.text=it.data!!.name
                    binding.tvEmailProfile.text=it.data!!.email
                }
                is UiState.Error->{
                    Toast.makeText(context,it.error.toString(),Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
        viewModel.uploadProfileState.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Error -> {
                    Toast.makeText(context,it.error.toString(),Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
        viewModel.deleteAccountState.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    toast(it.data!!)
                    deleteSharedPreference()
                    startActivity(
                        Intent(requireContext(), OnboardingActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    )
                }
                is UiState.Error -> {
                    toast(it.error!!)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {

    }
}