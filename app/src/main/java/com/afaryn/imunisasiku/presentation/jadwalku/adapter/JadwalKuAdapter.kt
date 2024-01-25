package com.afaryn.imunisasiku.presentation.jadwalku.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.ItemJadwalkuBinding
import com.afaryn.imunisasiku.model.Imunisasi
import com.afaryn.imunisasiku.utils.toToday
import com.afaryn.imunisasiku.utils.translateDateToIndonesian

class JadwalKuAdapter: RecyclerView.Adapter<JadwalKuAdapter.JadwalKuViewHolder>() {

    inner class JadwalKuViewHolder(val binding: ItemJadwalkuBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(imunisasi: Imunisasi) {
            binding.apply {
                imunisasi.pasien?.let {
                    tvNamaPasien.text = imunisasi.pasien.name
                    tvUmurPasien.text = imunisasi.pasien.tanggalLahir?.toToday() ?: "-"
                    tvJamImunisasi.text = imunisasi.jamImunisasi
                    tvJenisImunisasi.text = binding.root.context.getString(R.string.jadwalku_imunisasi, imunisasi.namaImunisasi)
                    val dateString =
                        if (imunisasi.jadwalImunisasi != null) translateDateToIndonesian(imunisasi.jadwalImunisasi)
                        else "-"
                    tvTanggalImunisasi.text = dateString

                    val parts = dateString.split(" ")
                    val dayOfMonth = parts[1].toInt()
                    val monthAbbreviation = parts[2].substring(0, 3)
                    val shortDate = binding.root.context.getString(R.string.short_imunisasi_date, dayOfMonth.toString(), monthAbbreviation)
                    tvShortDate.text = shortDate
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JadwalKuViewHolder {
        return JadwalKuViewHolder(
            ItemJadwalkuBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: JadwalKuViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)

        holder.binding.btnBatalkanImunisasi.setOnClickListener {
            onDeleteClick?.invoke(item)
        }
    }

    var onDeleteClick: ((Imunisasi) -> Unit)? = null
}