package com.afaryn.imunisasiku.presentation.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.databinding.FragmentMainBinding
import com.afaryn.imunisasiku.presentation.pasien.PasienActivity

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setActions()
    }

    private fun setActions() {
        binding.apply {
            cvPasien.setOnClickListener {
                startActivity(Intent(requireContext(), PasienActivity::class.java))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}