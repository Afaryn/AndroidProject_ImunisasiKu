package com.afaryn.imunisasiku.admin.ui.kelolaAkun.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.EditAkunPengguna
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.EditAkunPengguna.Companion.USER_EMAIL
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.EditAkunPengguna.Companion.USER_JK
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.EditAkunPengguna.Companion.USER_NAME
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.EditAkunPengguna.Companion.USER_PHONE
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.EditAkunPengguna.Companion.USER_ROLE
import com.afaryn.imunisasiku.admin.ui.profile.EditProfile
import com.afaryn.imunisasiku.databinding.ItemKelolaAkunBinding
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.model.User




class KelAkunAdpater : RecyclerView.Adapter<KelAkunAdpater.MyViewHolder>() {
    inner class MyViewHolder(val binding :ItemKelolaAkunBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(item: User){
            binding.apply {
                tvNamaAkun.text=item.name
                tvEmailAkun.text=item.email
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.email== newItem.email
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemKelolaAkunBinding.inflate(
                LayoutInflater.from(parent.context),null,false
            )
        )
    }

    override fun getItemCount(): Int =differ.currentList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.bind(item)

        val context = holder.itemView.context
        holder.binding.btnEdit.setOnClickListener{
            Intent(context,EditAkunPengguna::class.java).also{
                it.putExtra(USER_EMAIL,item.email)
                it.putExtra(USER_NAME,item.name)
                it.putExtra(USER_JK,item.jenisKelamin)
                it.putExtra(USER_PHONE,item.phone)
                it.putExtra(USER_ROLE,item.role)
                context.startActivity(it)
            }
        }
        holder.binding.btnDel.setOnClickListener {

        }
    }
}