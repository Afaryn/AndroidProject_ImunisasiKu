package com.afaryn.imunisasiku.admin.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.afaryn.imunisasiku.admin.ui.home.viewModel.HomeAdminViewModel
import com.afaryn.imunisasiku.admin.ui.kelolaAkun.KelolaAkun
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.KelolaImunisasi
import com.afaryn.imunisasiku.databinding.FragmentHomeAdminBinding
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.hide
import com.afaryn.imunisasiku.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragmentAdmin : Fragment() {
    private var _binding: FragmentHomeAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeAdminViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        val view = binding.root

        setAction()
        setPage()

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

    private fun setPage(){
        binding.apply {
            viewModel.getData()

            viewModel.getState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> {
                        // Menampilkan progress bar
                        if (state.isLoading == true) {
                            textView.hide()
                            tvChartName.hide()
                            lineChart.hide()
                            cardView.hide()
                            textView12.hide()
                            linearLayout12.hide()
                            progressBar.show()
                        }
                        else {
                            textView.show()
                            tvChartName.show()
                            lineChart.show()
                            cardView.show()
                            textView12.show()
                            linearLayout12.show()
                            progressBar.hide()
                        }
                    }
                    is UiState.Success -> {

                        // Mendapatkan data sebagai map
                        val data = state.data
                        // Menampilkan data pada UI
                        tvAkun.text = data!!["akun"]
                        tvLayanan.text = data["imunisasi"]
                        tvAntrian.text = data["pasien"]
                        textView.text = "Selamat Datang, "+data["username"]
                    }
                    is UiState.Error -> {

                        // Menampilkan pesan error
                        Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            viewModel.chartData()

            viewModel.chartState.observe(viewLifecycleOwner, Observer { chartDataState ->
                when (chartDataState) {
                    is UiState.Loading -> {
                        // Handle loading state jika diperlukan
                    }
                    is UiState.Success -> {
                        val chartData = chartDataState.data
                        updateChart(chartData!!)
                    }
                    is UiState.Error -> {
                        // Handle error state jika diperlukan
                        val errorMessage = chartDataState.error
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }


    }

    private fun updateChart(chartData: List<Pair<String, Float>>) {
        val lineSet = chartData

        // Set up the chart properties
        binding.lineChart.apply {
            // Set gradient fill colors
            gradientFillColors = intArrayOf(
                Color.parseColor("#1D2089"),
                Color.TRANSPARENT
            )

            // Set animation duration
            animation.duration = 1000L // Gantilah dengan nilai yang sesuai

            // Set data point touch listener
            onDataPointTouchListener = { index, _, _ ->
                val selectedData = chartData[index].second.toString()
                binding.tvChartData.text = selectedData
            }

            // Animate the chart with the data
            animate(lineSet)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        private const val animationDuration = 1000L
    }

}