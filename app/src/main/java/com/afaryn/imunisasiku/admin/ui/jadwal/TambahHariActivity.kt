package com.afaryn.imunisasiku.admin.ui.jadwal

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.jadwal.viewModel.jadwalViewModel
import com.afaryn.imunisasiku.databinding.ActivityTambahHariBinding
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList


@AndroidEntryPoint
class TambahHariActivity : AppCompatActivity() {

    private lateinit var binding :ActivityTambahHariBinding
    private val viewModel by viewModels<jadwalViewModel>()

    private var items = listOf<String>()
    private var data = JenisImunisasi()
    private var pickedDate: Date? = null
    private var pickedhari:String?=null
    private val jadwalBaru = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahHariBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observe()
        viewModel.getImn()
        setAction()
        binding.include.tvToolbar.text="Tambah Jadwal Hari"
    }

    @Suppress("DEPRECATION")
    private fun setAction(){
        binding.apply {
            include.btnBack.setOnClickListener {
                onBackPressed()
            }
            pickDateInput.setOnClickListener {
                setupDatePicker()
            }
            button2.setOnClickListener {
                if (data.jadwalImunisasi!=null){
                    val jadwal = data.jadwalImunisasi!!.map { it }
                    jadwalBaru.addAll(jadwal)
                }
                if (data.siklus=="bulan"){
                    jadwalBaru.add(pickedDate.toString())
                    val dataUnik = jadwalBaru.distinct()
                    viewModel.sendHari(data.namaImunisasi!!,dataUnik)
                }
                if (data.siklus=="minggu"){
                    if (cbSenin2.isChecked){
//                        pickedhari = "Senin"
                        jadwalBaru.add("Senin")
                    }
                    if(cbSelasa2.isChecked){
//                        pickedhari="Selasa"
                        jadwalBaru.add("Selasa")
                    }
                    if(cbRabu2.isChecked){
//                        pickedhari="Rabu"
                        jadwalBaru.add("Rabu")
                    }
                    if(cbKamis2.isChecked){
//                        pickedhari="Kamis"
                        jadwalBaru.add("Kamis")
                    }
                    if(cbJumat2.isChecked){
//                        pickedhari="Jum'at"
                        jadwalBaru.add("Jum'at")
                    }
                    if (cbSabtu2.isChecked){
//                        pickedhari="Sabtu"
                        jadwalBaru.add("Sabtu")
                    }
                    val dataUnik = jadwalBaru.distinct()
                    viewModel.sendHari(data.namaImunisasi!!,dataUnik)
                }
            }
        }
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
//                        Toast.makeText(this,"item selected :${itemSelected.toString()}", Toast.LENGTH_SHORT).show()
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
                    if(it.data.siklus=="bulan"){
                        binding.linearLayout10.isVisible=true
                        binding.layoutMinggu.isVisible = false
                    }
                    if(it.data.siklus=="minggu"){
                        binding.linearLayout10.isVisible=false
                        binding.layoutMinggu.isVisible = true
                    }
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
            val dateFormat = SimpleDateFormat("dd MMMM ", Locale.getDefault())

            pickedDate = calendar.time
            binding.tvPickupDate.text = dateFormat.format(calendar.time)
        }, year, month, day)

        dpd.show()
    }

}