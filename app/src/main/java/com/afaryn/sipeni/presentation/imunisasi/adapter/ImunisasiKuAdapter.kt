package com.afaryn.sipeni.presentation.imunisasi.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.sipeni.databinding.ItemImunisasikuBinding
import com.afaryn.sipeni.model.Imunisasi
import com.afaryn.sipeni.utils.toToday
import com.afaryn.sipeni.utils.translateDateToIndonesian

class ImunisasiKuAdapter: RecyclerView.Adapter<ImunisasiKuAdapter.ImunisasiKuViewHolder>() {

    inner class ImunisasiKuViewHolder(val binding: ItemImunisasikuBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(imunisasi: Imunisasi) {
            binding.apply {
                imunisasi.pasien?.let {
                    tvNamaPasien.text = imunisasi.pasien.name
                    tvUsia.text = imunisasi.pasien.tanggalLahir?.toToday() ?: "-"
                    tvCatatan.text = imunisasi.pasien.catatan
                    tvJenisKelamin.text = if (imunisasi.pasien.jenisKelamin == "Laki - laki") "(L)" else ("(P)")

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImunisasiKuViewHolder {
        return ImunisasiKuViewHolder(
            ItemImunisasikuBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ImunisasiKuViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)

        holder.binding.btnBatalkanImunisasi.setOnClickListener {
            onDeleteClick?.invoke(item)
        }
    }

    var onDeleteClick: ((Imunisasi) -> Unit)? = null
}