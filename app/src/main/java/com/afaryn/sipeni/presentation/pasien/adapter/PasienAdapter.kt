package com.afaryn.sipeni.presentation.pasien.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.sipeni.databinding.ItemPasienBinding
import com.afaryn.sipeni.model.Pasien
import com.afaryn.sipeni.utils.hide
import com.afaryn.sipeni.utils.show
import com.afaryn.sipeni.utils.toToday

class PasienAdapter(private var pickPasien: Boolean = false): RecyclerView.Adapter<PasienAdapter.PasienViewHolder>() {

    inner class PasienViewHolder(val binding: ItemPasienBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(pasien: Pasien) {
            binding.apply {
                if (pickPasien) {
                    layoutEditPasien.hide()
                    btnPilihPasien.show()
                }

                tvNamaPasien.text = pasien.name
                tvUsia.text = pasien.tanggalLahir?.toToday() ?: "-"
                tvCatatan.text = pasien.catatan
                tvJenisKelamin.text = if (pasien.jenisKelamin == "Laki - laki") "(L)" else ("(P)")
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Pasien>() {
        override fun areItemsTheSame(oldItem: Pasien, newItem: Pasien): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Pasien, newItem: Pasien): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasienViewHolder {
        return PasienViewHolder(
            ItemPasienBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: PasienViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)

        holder.binding.btnPilihPasien.setOnClickListener {
            onPickPasienClick?.invoke(item)
        }
        holder.binding.btnEditPasien.setOnClickListener {
            onEditPasienClick?.invoke(item)
        }
        holder.binding.btnHapus.setOnClickListener {
            onDeletePasienClick?.invoke(item)
        }
    }

    var onPickPasienClick: ((Pasien) -> Unit)? = null
    var onEditPasienClick: ((Pasien) -> Unit)? = null
    var onDeletePasienClick:  ((Pasien) -> Unit)? = null
}