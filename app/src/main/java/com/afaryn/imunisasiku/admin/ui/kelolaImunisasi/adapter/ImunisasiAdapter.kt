package com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.EditImunisasi
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.EditImunisasi.Companion.BATAS_USIA
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.EditImunisasi.Companion.IMUNISASI
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.EditImunisasi.Companion.JADWAL_IMUNISASI
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.EditImunisasi.Companion.JAM_IMUNISASI
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.TambahImunisasi
import com.afaryn.imunisasiku.databinding.ItemJenisImunisasiBinding
import com.afaryn.imunisasiku.model.JenisImunisasi

class ImunisasiAdapter:RecyclerView.Adapter<ImunisasiAdapter.MyViewHolder>() {
    inner class MyViewHolder( val binding:ItemJenisImunisasiBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(item:JenisImunisasi){
            binding.apply {
                tvNamaImunisasi.text=item.namaImunisasi
                tvUsiaImunisasi.text = item.batasUmur.toString()
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<JenisImunisasi>() {
        override fun areItemsTheSame(oldItem: JenisImunisasi, newItem: JenisImunisasi): Boolean {
            return oldItem.namaImunisasi== newItem.namaImunisasi
        }

        override fun areContentsTheSame(oldItem: JenisImunisasi, newItem: JenisImunisasi): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemJenisImunisasiBinding.inflate(
                LayoutInflater.from(parent.context),null,false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.bind(item)


        val context = holder.itemView.context
        holder.binding.btnEdit.setOnClickListener {
            Intent(context, EditImunisasi::class.java).also {
                it.putExtra(IMUNISASI, item.namaImunisasi)
                it.putExtra(BATAS_USIA,item.batasUmur.toString())
                it.putExtra(JADWAL_IMUNISASI,item.jadwalImunisasi)
                it.putExtra(JAM_IMUNISASI,item.jamImunisasi)

                context.startActivity(it)
            }
        }
    }
}
