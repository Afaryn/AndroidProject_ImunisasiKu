package com.afaryn.sipeni.admin.ui.jadwal

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import com.afaryn.sipeni.R
import com.afaryn.sipeni.admin.ui.jadwal.viewModel.jadwalViewModel
import com.afaryn.sipeni.databinding.ActivityTambahJamBinding
import com.afaryn.sipeni.model.JenisImunisasi
import com.afaryn.sipeni.utils.UiState
import com.afaryn.sipeni.utils.hide
import com.afaryn.sipeni.utils.show
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class TambahJamActivity : AppCompatActivity(){


    private lateinit var binding:ActivityTambahJamBinding
    private val viewModel by viewModels<jadwalViewModel>()

    private var items = listOf<String>()
    private val jadwalBaru = ArrayList<String>()
    private var pickedTime:String?=null
    private var data = JenisImunisasi()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahJamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAction()
        viewModel.getImn()
        observe()
        binding.include.tvToolbar.text="Tambah Jadwal Jam"
    }

    private fun observe(){
        viewModel.getState.observe(this){
            when(it){
                is UiState.Loading->{
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success->{
                    val datas = it.data!!
                    items = datas.map { it.namaImunisasi!! }
                    val autoCompleteTextView : AutoCompleteTextView = binding.autoComplete
                    val adapter = ArrayAdapter(this,R.layout.list_item_imunisasi,items)

                    autoCompleteTextView.setAdapter(adapter)
                    autoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener {
                            adapterView, view, i, l ->
                        val itemSelected = adapterView.getItemAtPosition(i)
                        viewModel.getSiklus(itemSelected.toString())
                    }
                }
                is UiState.Error->{
                    Toast.makeText(this,"Terdapat Error",Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.getSiklusState.observe(this){
            when(it){
                is UiState.Loading->{
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success->{
                    data = it.data!!
                }
                is UiState.Error->{
                    Toast.makeText(this,"Terdapat Error",Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.sendState.observe(this){
            when(it){
                is UiState.Loading->{
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success->{
                    Toast.makeText(this,it.data,Toast.LENGTH_SHORT).show()
                    finish()
                }
                is UiState.Error->{
                    Toast.makeText(this,"Terdapat Error",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    @Suppress("DEPRECATION")
    private fun setAction(){
        binding.apply {
            include.btnBack.setOnClickListener {
                onBackPressed()
            }
            etJamMulai.setOnClickListener {
                getTime()

            }
            etJamSelesai.setOnClickListener {
                getTime()
            }
            button2.setOnClickListener {
                val mulai = etJamMulai.text.toString()
                val selesai = etJamSelesai.text.toString()
                val jamBaru = "$mulai-$selesai"
                if (data.jamImunisasi!=null){
                    val jadwal = data.jamImunisasi!!.map { it }
                    jadwalBaru.addAll(jadwal)
                }
                jadwalBaru.add(jamBaru)
                val dataUnik = jadwalBaru.distinct()
                viewModel.sendJam(data.namaImunisasi!!,dataUnik)
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

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

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
}