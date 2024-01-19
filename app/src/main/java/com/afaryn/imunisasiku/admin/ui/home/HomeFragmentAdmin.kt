package com.afaryn.imunisasiku.admin.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.KelolaAkun
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.KelolaImunisasi
import com.afaryn.imunisasiku.databinding.FragmentHomeAdminBinding

class HomeFragmentAdmin : Fragment() {
    private lateinit var binding: FragmentHomeAdminBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        val view = binding.root

        setAction()

        return view
    }

    private fun setAction(){
        binding.apply {
            CvImunisasi.setOnClickListener{
                val intent = Intent(requireActivity(), KelolaImunisasi::class.java)
                requireActivity().startActivity(intent)
            }
            CvAkun.setOnClickListener{val intent = Intent(requireActivity(), KelolaAkun::class.java)
                requireActivity().startActivity(intent)
            }
        }
    }
}