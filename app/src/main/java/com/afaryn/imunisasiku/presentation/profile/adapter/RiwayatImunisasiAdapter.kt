package com.afaryn.imunisasiku.presentation.profile.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.imunisasiku.databinding.ItemRiwayatImunisasiBinding
import com.afaryn.imunisasiku.model.Imunisasi
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import com.afaryn.imunisasiku.utils.stringToDate
import com.afaryn.imunisasiku.utils.translateDateToIndonesian
import java.util.Date

class RiwayatImunisasiAdapter: RecyclerView.Adapter<RiwayatImunisasiAdapter.RiwayatImunisasiViewHolder>() {

    inner class RiwayatImunisasiViewHolder(val binding: ItemRiwayatImunisasiBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(imunisasi: Imunisasi) {
            binding.apply {
                imunisasi.pasien?.let {
                    tvNama.text = imunisasi.pasien.name
                    tvJenisImunisasi.text = imunisasi.namaImunisasi

                    val dateString =
                        if (imunisasi.jadwalImunisasi != null) translateDateToIndonesian(imunisasi.jadwalImunisasi)
                        else "-"

                    if (dateString != "-") {
                        val parts = dateString.split(" ")
                        val dayOfMonth = parts[1].toInt()
                        val month = parts[2]
                        val year = parts[3]
                        tvTanggalImunisasi.text = "$dayOfMonth $month $year"
                    }

                    // Cek tanggal sudah lewat atau belum
                    imunisasi.jadwalImunisasi?.let {
                        val date = stringToDate(imunisasi.jadwalImunisasi)
                        val currentDate = Date()

                        if (date.time < currentDate) {
                            layoutBelumTerlewati.hide()
                            layoutTerlewati.show()
                        } else {
                            layoutBelumTerlewati.show()
                            layoutTerlewati.hide()
                        }
                    }
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Imunisasi>() {
        override fun areItemsTheSame(oldItem: Imunisasi, newItem: Imunisasi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Imunisasi, newItem: Imunisasi): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatImunisasiViewHolder {
        return RiwayatImunisasiViewHolder(
            ItemRiwayatImunisasiBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: RiwayatImunisasiViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

}