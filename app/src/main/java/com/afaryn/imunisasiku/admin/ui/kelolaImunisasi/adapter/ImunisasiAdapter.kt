package com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.EditImunisasi
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.EditImunisasi.Companion.BATAS_USIA
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.EditImunisasi.Companion.IMUNISASI
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.EditImunisasi.Companion.JADWAL_IMUNISASI
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.EditImunisasi.Companion.JAM_IMUNISASI
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.TambahImunisasi
import com.afaryn.imunisasiku.databinding.ItemJenisImunisasiBinding
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint


class ImunisasiAdapter:RecyclerView.Adapter<ImunisasiAdapter.MyViewHolder>() {

//    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
//    private var dialog: Dialog? = null
//
//    // Fungsi untuk menyimpan atau memperbarui data ke Firestore
//    private fun updateFirestore(item: JenisImunisasi) {
//        // Di sini, sesuaikan dengan struktur Firestore dan logika penyimpanan Anda
//        val userReference = firestore.collection(Constants.JENIS_IMUNISASI).document(item.namaImunisasi!!)
//
//        // Contoh: Menghapus data dari Firestore
//        userReference.delete()
//            .addOnSuccessListener {
//                Toast.makeText(context,"Berhasil Menghapus data",Toast.LENGTH_SHORT).show()
//                if(dialog!!.isShowing){
//                    dialog!!.dismiss()
//                }
//            }
//            .addOnFailureListener {
//                Toast.makeText(context,it.message.toString(),Toast.LENGTH_SHORT).show()
//                // Tambahkan logika jika penyimpanan gagal
//            }
//    }
//
//    // Fungsi untuk menghapus item dari daftar
//    fun deleteItem(position: Int) {
//        val tempList = differ.currentList.toMutableList()
//        val deletedItem = tempList.removeAt(position)
//        differ.submitList(tempList)
//
//        // Panggil fungsi untuk menyimpan atau memperbarui data ke Firestore
//        updateFirestore(deletedItem)
//    }

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
        holder.binding.btnDel.setOnClickListener {
//            showCustomDialogBox("Apakah Ingin menghapus Imunisasi ${item.namaImunisasi}",position)
            onDeleteClick?.invoke(item)
        }
    }

    var onDeleteClick:((JenisImunisasi)-> Unit)? = null

}
