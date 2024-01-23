package com.afaryn.imunisasiku.presentation.imunisasi

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.ActivityDaftarImunisasiBinding
import com.afaryn.imunisasiku.model.Imunisasi
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.model.Pasien
import com.afaryn.imunisasiku.notification.NotificationWorker
import com.afaryn.imunisasiku.presentation.imunisasi.viewmodel.DaftarImunisasiViewModel
import com.afaryn.imunisasiku.presentation.pasien.PasienActivity
import com.afaryn.imunisasiku.presentation.pasien.PasienActivity.Companion.PASIEN_PICKED
import com.afaryn.imunisasiku.utils.Constants.CYCLE_MONTHLY
import com.afaryn.imunisasiku.utils.Constants.PICK_PASIEN
import com.afaryn.imunisasiku.utils.Constants.REQUEST_PICK_PASIEN
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.getDayOfWeek
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.isSameDay
import com.afaryn.imunisasiku.utils.parseDateString
import com.afaryn.imunisasiku.utils.show
import com.afaryn.imunisasiku.utils.stringToDate
import com.afaryn.imunisasiku.utils.toToday
import com.afaryn.imunisasiku.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DaftarImunisasiActivity : AppCompatActivity() {

    private var _binding: ActivityDaftarImunisasiBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DaftarImunisasiViewModel>()
    private var listJenisImunisasi: List<JenisImunisasi> = listOf()
    private var selectedImunisasi: JenisImunisasi = JenisImunisasi()
    private var selectedJadwal: String? = null
    private var selectedJam: String? = null
    private var pasien: Pasien? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDaftarImunisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
        observer()
    }

    private fun setActions() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            layoutPickPasien.setOnClickListener {
                startActivityForResult(
                    Intent(this@DaftarImunisasiActivity, PasienActivity::class.java).also {
                        it.putExtra(PICK_PASIEN, true)
                    }, REQUEST_PICK_PASIEN
                )
            }

            btnSimpan.setOnClickListener {
                when {
                    selectedJadwal.isNullOrEmpty() -> {
                        toast("Pilih jadwal imunisasi yang sesuai")
                        return@setOnClickListener
                    }
                    selectedJadwal!!.contains("Tidak ada jadwal") -> {
                        toast("Jadwal untuk imunisasi yang dipilih tidak tersedia")
                        return@setOnClickListener
                    }
                    pasien == null -> {
                        toast("Harap pilih pasien untuk imunisasi")
                        return@setOnClickListener
                    }
                    selectedJam.isNullOrEmpty() -> {
                        toast("Harap pilih jam imunisasi")
                        return@setOnClickListener
                    }
                    else -> {
                        if (selectedImunisasi.siklus != CYCLE_MONTHLY) {
                            val selectedDayOfWeek = getDayOfWeek(selectedJadwal!!)
                            if (selectedDayOfWeek != -1) {
                                val currentDate = Calendar.getInstance()
                                val todayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)

                                val daysUntilNextOccurrence = (selectedDayOfWeek - todayOfWeek + 8) % 7
                                if (daysUntilNextOccurrence == todayOfWeek) {
                                    toast("Kacau")
                                    return@setOnClickListener
                                }

                                val nextOccurrence = Calendar.getInstance()
                                nextOccurrence.add(Calendar.DAY_OF_MONTH, daysUntilNextOccurrence)
                                selectedJadwal = parseDateString(nextOccurrence.time.toString())
                            }
                        }

                        if (isSameDay(selectedJadwal!!)) {
                            toast("Tidak dapat daftar imunisasi pada hari yang sama")
                            return@setOnClickListener
                        } else if (isImunisasiHasPassed()) {
                            toast("Jadwal imunisasi sudah lewat")
                            return@setOnClickListener
                        }

                        val imunisasi = Imunisasi(
                            pasien = pasien,
                            namaImunisasi = selectedImunisasi.namaImunisasi,
                            jadwalImunisasi = selectedJadwal,
                            jamImunisasi = selectedJam
                        )
                        viewModel.daftarImunisasi(imunisasi)
                    }
                }
            }
        }
    }

    private fun isImunisasiHasPassed(): Boolean {
        val stringToDate = stringToDate(selectedJadwal!!)
        val today = Date()

        return stringToDate.time < today
    }

    private fun observer() {
        viewModel.imunisasiListState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    it.data?.let { jenisImunisasi ->
                        listJenisImunisasi = jenisImunisasi
                        setupSpinner(jenisImunisasi)
                    }
                }

                is UiState.Error -> {
                    toast(it.error ?: "Terjadi Kesalahan")
                }
            }
        }

        viewModel.daftarImunisasiState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) {
                        binding.progressBar.show()
                    } else {
                        binding.progressBar.hide()
                    }
                }
                is UiState.Success -> {
                    setNotifikasi(it.data!!)
                    toast("Berhasil daftar imunisasi")
                    finish()
                }
                is UiState.Error -> {
                    toast(it.error ?: "Terjadi Kesalahan")
                }
            }
        }
    }

    private fun setupSpinner(jenisImunisasi: List<JenisImunisasi>) {
        val namaJenisImunisasi = mutableListOf<String>()
        val jadwalImunisasi = mutableListOf<String>()
        val jamImunisasi = mutableListOf<String>()

        jenisImunisasi.forEach {
            namaJenisImunisasi.add(it.namaImunisasi ?: "")
        }
        val jenisImunisasiAdapter = ArrayAdapter(this, R.layout.item_spinner, namaJenisImunisasi)
        val waktuImunisasiAdapter = ArrayAdapter(this, R.layout.item_spinner, jadwalImunisasi)
        val jamImunisasiAdapter = ArrayAdapter(this, R.layout.item_spinner, jamImunisasi)

        binding.apply {
            acJenisImunisasi.setAdapter(jenisImunisasiAdapter)
            acTanggalImunisasi.setAdapter(waktuImunisasiAdapter)
            acJamImunisasi.setAdapter(jamImunisasiAdapter)

            acJenisImunisasi.setOnItemClickListener { _, _, position, _ ->
                selectedJadwal = ""
                selectedImunisasi = jenisImunisasi[position]

                jadwalImunisasi.clear()
                jamImunisasi.clear()
                if (selectedImunisasi.jadwalImunisasi.isNullOrEmpty()) {
                    jadwalImunisasi.add("Tidak ada jadwal untuk imunisasi ${selectedImunisasi.namaImunisasi} saat ini")
                } else {
                    selectedImunisasi.jadwalImunisasi?.forEach {
                        val jadwal = if (selectedImunisasi.siklus == CYCLE_MONTHLY) {
                            parseDateString(it)
                        } else it

                        jadwalImunisasi.add(jadwal)
                    }
                }

                if (selectedImunisasi.jamImunisasi.isNullOrEmpty()) {
                    jamImunisasi.add("Tidak ada jam untuk imunisasi ${selectedImunisasi.namaImunisasi} saat ini")
                } else {
                    selectedImunisasi.jamImunisasi?.forEach {
                        jamImunisasi.add(it)
                    }
                }
            }

            acTanggalImunisasi.setOnItemClickListener { parent, _, position, _ ->
                selectedJadwal = parent.getItemAtPosition(position).toString()
            }

            acJamImunisasi.setOnItemClickListener { parent, _, position, _ ->
                selectedJam = parent.getItemAtPosition(position).toString()
            }
        }
    }

    private fun setNotifikasi(imunisasiId: String) {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        try {
            val date = dateFormat.parse(selectedJadwal!!)
            if (date != null) {
                scheduleOneTimeWorker(date.time, imunisasiId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun scheduleOneTimeWorker(timeInMillis: Long, imunisasiId: String) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val selectedTimeInMillis = calendar.timeInMillis - System.currentTimeMillis()
        val notificationMessage = "Besok Pada Pukul $selectedJam"

        val inputData = workDataOf(
            NotificationWorker.NOTIFICATION_TITLE to selectedImunisasi.namaImunisasi,
            NotificationWorker.WAKTU_IMUNISASI to notificationMessage
        )

        val notificationManager =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .addTag(imunisasiId)
                .setInputData(inputData)
                .setInitialDelay(selectedTimeInMillis, TimeUnit.MILLISECONDS).build()

        WorkManager.getInstance(this).enqueue(notificationManager)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_PASIEN) {
            val dataPasien = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data?.getParcelableExtra(PASIEN_PICKED, Pasien::class.java)
            } else {
                data?.getParcelableExtra(PASIEN_PICKED)
            }

            dataPasien?.let {
                pasien = it
                binding.apply {
                    layoutPickPasien.hide()
                    tvNamaPasien.text = it.name
                    tvUsia.text = it.tanggalLahir?.toToday() ?: "-"
                    tvCatatan.text = it.catatan
                    tvJenisKelamin.text = if (it.jenisKelamin == "Laki - laki") "(L)" else ("(P)")
                    layoutPasien.show()

                    icDeletePasien.setOnClickListener {
                        pasien = null
                        layoutPasien.hide()
                        layoutPickPasien.show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}