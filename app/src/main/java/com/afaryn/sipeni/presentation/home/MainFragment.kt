package com.afaryn.sipeni.presentation.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.afaryn.sipeni.R
import com.afaryn.sipeni.databinding.FragmentMainBinding
import com.afaryn.sipeni.model.Imunisasi
import com.afaryn.sipeni.presentation.home.viewmodel.HomeViewModel
import com.afaryn.sipeni.presentation.imunisasi.DaftarImunisasiActivity
import com.afaryn.sipeni.presentation.imunisasi.ImunisasiKuActivity
import com.afaryn.sipeni.presentation.jadwalku.JadwalkuActivity
import com.afaryn.sipeni.presentation.notification.NotificationActivity
import com.afaryn.sipeni.presentation.pasien.PasienActivity
import com.afaryn.sipeni.utils.UiState
import com.afaryn.sipeni.utils.hide
import com.afaryn.sipeni.utils.show
import com.afaryn.sipeni.utils.toast
import com.afaryn.sipeni.utils.translateDateToIndonesian
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setActions()
        observer()
    }

    private fun setActions() {
        binding.apply {
            cvPasien.setOnClickListener {
                startActivity(Intent(requireContext(), PasienActivity::class.java))
            }
            cvImunisasiku.setOnClickListener {
                startActivity(Intent(requireContext(), ImunisasiKuActivity::class.java))
            }
            cvJadwalku.setOnClickListener {
                startActivity(Intent(requireContext(), JadwalkuActivity::class.java))
            }
            btnDaftarImunisasi.setOnClickListener {
                startActivity(Intent(requireContext(), DaftarImunisasiActivity::class.java))
            }
            btnNotification.setOnClickListener {
                startActivity(Intent(requireContext(), NotificationActivity::class.java))
            }
            btnHubungiAdmin.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/628195534505")))
            }
            cvAyoImunisasi.setOnClickListener {
                startActivity(Intent(requireContext(), PosterActivity::class.java).apply {
                    putExtra(
                        PosterActivity.POSTER_INTENT,
                        PosterActivity.AYO_IMUNISASI
                    )
                })
            }
            cvJadwalImunisasi.setOnClickListener {
                startActivity(Intent(requireContext(), PosterActivity::class.java).apply {
                    putExtra(
                        PosterActivity.POSTER_INTENT,
                        PosterActivity.JADWAL_IMUNISASI
                    )
                })
            }
            cvBidanKami.setOnClickListener {
                startActivity(Intent(requireContext(), PosterActivity::class.java).apply {
                    putExtra(
                        PosterActivity.POSTER_INTENT,
                        PosterActivity.BIDAN_KAMI
                    )
                })
            }
        }
    }

    private fun observer() {
        viewModel.userState.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Success -> {
                    binding.tvHello.text = getString(R.string.hai_user, it.data)
                }

                else -> {
                    binding.tvHello.text = getString(R.string.hai_user, "-")
                }
            }
        }

        viewModel.homeState.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Success -> {
                    if (it.data?.jadwalImunisasi != null) {
                        setClosestImunisasi(it.data)
                    } else {
                        binding.layoutNoSchedule.show()
                        binding.layoutImunisasiTerdekat.hide()
                    }
                }

                is UiState.Error -> {
                    toast(it.error ?: "Terjadi Kesalahan")
                }

                else -> {}
            }
        }
    }

    private fun setClosestImunisasi(imunisasi: Imunisasi) {
        binding.apply {
            binding.layoutNoSchedule.hide()
            binding.tvNamaPasien.text = imunisasi.pasien?.name ?: "-"
            binding.tvJenisImunisasi.text = imunisasi.namaImunisasi
            binding.tvTanggalImunisasi.text =
                if (imunisasi.jadwalImunisasi != null) translateDateToIndonesian(imunisasi.jadwalImunisasi)
                else "-"

            // Get remaining days
            if (imunisasi.jadwalImunisasi != null) {
                val today = LocalDate.now()
                val daysRemaining = ChronoUnit.DAYS.between(today, imunisasi.jadwalImunisasi.toInstant().atZone(
                    ZoneId.systemDefault()).toLocalDate())
                binding.tvDayCount.text = daysRemaining.toString()
                binding.layoutImunisasiTerdekat.show()
            } else {
                binding.tvDayCount.text = "-"
                binding.layoutImunisasiTerdekat.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}