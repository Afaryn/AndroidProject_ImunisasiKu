package com.afaryn.imunisasiku.admin.ui.kelolaImunisasi

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.home.HomeAdminActivity
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.viewModel.TambahImnViewModel
import com.afaryn.imunisasiku.databinding.ActivityTambahImunisasiBinding
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Suppress("DEPRECATION")
@AndroidEntryPoint
class TambahImunisasi : AppCompatActivity() {

    private val viewModel by viewModels<TambahImnViewModel>()
    private var _binding: ActivityTambahImunisasiBinding?=null
    private val binding get()=_binding!!
    private var pickedDate = arrayListOf<Date>()
    private var jamImunisasi = arrayListOf<String>()
    private var pickedTime:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityTambahImunisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ToolbarName()
        setAction()
        observer()
    }


    private fun setAction(){
        with(binding){
            etJamMulai.setOnClickListener {
                getTime()
            }
            etJamSelesai.setOnClickListener {
                getTime()
            }
            include.btnBack.setOnClickListener{
                onBackPressed()
            }
            pickDateInput.setOnClickListener {
                setupDatePicker()
            }
            button2.setOnClickListener{

                if(!validateFields()){
                    Toast.makeText(this@TambahImunisasi,"Harap isi semua kolom",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val nama = etNamaImunisasi.text.toString().trim()
                val usia = (etUsiaImunisasi.text.toString().trim()).toInt()

                val jamMulai = etJamMulai.text.toString()
                val jamSelesai = etJamSelesai.text.toString()

                val jam = "$jamMulai-$jamSelesai"
                jamImunisasi.add(jam)

                val tambahImn = JenisImunisasi(
                    namaImunisasi = nama,
                    batasUmur = usia,
                    jadwalImunisasi = pickedDate,
                    jamImunisasi = jamImunisasi,
                )
                viewModel.sendImunisasi(tambahImn)
            }

        }
    }

    private fun observer(){
        viewModel.sendingState.observe(this){
            when(it){
                is UiState.Loading->{
                    if (it.isLoading == true) binding.progressBar2.show()
                    else binding.progressBar2.hide()
                }
                is UiState.Success -> {
                    Toast.makeText(this,it.data!!,Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,HomeAdminActivity::class.java)
                    startActivity(intent)
//                    finish()
                }
                is UiState.Error -> {
                    Toast.makeText(this,it.error.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun getTime(){

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val min = c.get(Calendar.MINUTE)

        val dpd = TimePickerDialog(this, { _, h, m ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY,h)
            calendar.set(Calendar.MINUTE,m)

            val timeFormat = SimpleDateFormat("HH:mm",Locale.getDefault()).format(calendar.time)

            pickedTime = timeFormat
            binding.apply {
            if (etJamMulai.isFocused){
                etJamMulai.setText(pickedTime!!)
            }
            if (etJamSelesai.isFocused){
                etJamSelesai.setText(pickedTime!!)
            }
            } // Menggunakan timeFormat sebagai parameter
        }, hour, min, true) // Menggunakan hour dan min sebagai parameter, dan menambahkan parameter is24HourView

        dpd.show()
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
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val dateFormat = SimpleDateFormat("dd MMMM ", Locale.getDefault())

            pickedDate.add(calendar.time)
            binding.tvPickupDate.text = dateFormat.format(calendar.time)
        }, year, month, day)

        dpd.show()
    }

    private fun ToolbarName(){
        binding.include.tvToolbar.text="Tambah Imunisasi"
    }

    private fun validateFields(): Boolean {
        with(binding) {
            return etNamaImunisasi.text!!.isNotEmpty() && etUsiaImunisasi.text!!.isNotEmpty()
                    && etJamMulai.text.isNotEmpty() && etJamMulai.text.isNotEmpty()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}