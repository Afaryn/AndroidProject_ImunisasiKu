package com.afaryn.imunisasiku.presentation.pasien

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.imunisasiku.databinding.ActivityTambahPasienBinding
import com.afaryn.imunisasiku.model.Pasien
import com.afaryn.imunisasiku.presentation.pasien.viewmodel.PasienViewModel
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import com.afaryn.imunisasiku.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TambahPasienActivity : AppCompatActivity() {

    private var _binding: ActivityTambahPasienBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PasienViewModel>()
    private var pickedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTambahPasienBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
        observer()
    }

    private fun setActions() {
        with(binding) {
            pickDateInput.setOnClickListener {
                setupDatePicker()
            }

            btnBack.setOnClickListener {
                finish()
            }

            btnSimpan.setOnClickListener {
                if (checkFieldsValidation()) {
                    val selectedRadio = rgJenisKelamin.checkedRadioButtonId
                    val jenisKelamin = findViewById<RadioButton>(selectedRadio).text.toString()
                    val nama = etNama.text.toString().trim()
                    val tanggalLahir = pickedDate
                    val nik = etNik.text.toString().trim()
                    val catatan = etCatatan.text.toString().trim()
                    val pasien = Pasien(
                        name = nama,
                        jenisKelamin = jenisKelamin,
                        tanggalLahir = tanggalLahir,
                        nik = nik,
                        catatan = catatan
                    )

                    viewModel.addPatient(pasien)
                } else {
                    toast("Harap isi semua bagian")
                }
            }
        }
    }

    private fun checkFieldsValidation(): Boolean {
        binding.apply {
            return etNama.text.isNotEmpty() && etNik.text.isNotEmpty() &&
                    pickedDate != null && etCatatan.text.isNotEmpty() &&
                    rgJenisKelamin.checkedRadioButtonId != -1
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, { _, y, monthOfYear, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, y)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

            pickedDate = calendar.time
            binding.tvPickupDate.text = dateFormat.format(calendar.time)
        }, year, month, day)

        dpd.show()
    }

    private fun observer() {
        viewModel.addPatientState.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    toast(it.data!!)
                    finish()
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
}