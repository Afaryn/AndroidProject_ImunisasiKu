package com.afaryn.imunisasiku.presentation.profile

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.afaryn.imunisasiku.admin.ui.profile.BantuanActivity
import com.afaryn.imunisasiku.admin.ui.profile.SyaratKetentuanActivity
import com.afaryn.imunisasiku.databinding.FragmentUserProfileBinding
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.presentation.profile.viewmodel.ProfilViewModel
import com.afaryn.imunisasiku.utils.Constants.PERMISSIONS
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.glide
import com.afaryn.imunisasiku.utils.reduceFileImage
import com.afaryn.imunisasiku.utils.setupDeleteDialog
import com.afaryn.imunisasiku.utils.toast
import com.afaryn.imunisasiku.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfilViewModel>()
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
            permission.launch(PERMISSIONS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionSet()
        setActions()
        observer()
    }

    private fun setActions() {
        binding.apply {
            ivTakePicture.setOnClickListener {
                requireActivity().setupDeleteDialog(
                    title = "Ganti foto profil?",
                    message = "Apakah anda ingin mengganti foto profil saat ini",
                    btnActionText = "Ya"
                ) {
                    if (!permissionGiven) {
                        toast("Tidak dapat membuka galeri, Izin belum diberikan.")
                        return@setupDeleteDialog
                    }
                    val chooser = setUpGallery()
                    launchGallery.launch(chooser)
                }
            }

            layoutEditProfil.setOnClickListener {
                startActivity(Intent(context, UserEditProfileActivity::class.java))
            }

            layoutRiwayatImunisasi.setOnClickListener {
                startActivity(Intent(context, RiwayatImunisasiActivity::class.java))
            }

            layoutSyaratKetentuan.setOnClickListener {
                startActivity(Intent(context, SyaratKetentuanActivity::class.java))
            }

            layoutBantuan.setOnClickListener {
                startActivity(Intent(context, BantuanActivity::class.java))
            }
        }
    }

    private fun observer() {
        viewModel.profileState.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Success -> {
                    it.data?.let { user ->
                        dataUser = user
                        if (!user.profile.isNullOrEmpty()) {
                            binding.ivProfil.glide(user.profile)
                        }
                        binding.tvUsername.text = user.name
                        binding.tvEmail.text = user.email
                    }
                }

                is UiState.Error -> {
                    toast(it.error ?: "Terjadi kesalahan saat mengambil data")
                }

                else -> {}
            }
        }

        viewModel.uploadProfileState.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Error -> {
                    toast(it.error!!)
                }

                else -> {}
            }
        }
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

}