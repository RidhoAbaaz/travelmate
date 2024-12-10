package com.example.travelmate.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelmate.databinding.FragmentResultBinding
import com.example.travelmate.ui.adapter.ResultAdapter
import com.example.travelmate.data.model.RecommendationRequest
import com.example.travelmate.data.repository.RecommendationRepository
import com.example.travelmate.data.retrofit.ApiConfig

class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding
    private lateinit var resultViewModel: ResultViewModel
    private lateinit var adapter: ResultAdapter

    // Safe Args untuk menerima data yang dipassing
    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(inflater, container, false)

        // Inisialisasi ViewModel secara manual tanpa @AndroidEntryPoint
        val apiService = ApiConfig.getApiService()
        val repository = RecommendationRepository(apiService)
        val factory = ResultViewModelFactory(repository)
        resultViewModel = ViewModelProvider(this, factory)[ResultViewModel::class.java]

        // Inisialisasi adapter
        adapter = ResultAdapter { placeId ->
            // Pastikan 'placeId' dikirimkan dengan benar melalui Safe Args
            val action = ResultFragmentDirections.actionResultToDetail(placeId)
            findNavController().navigate(action)
        }

        binding.rvResult.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ResultFragment.adapter
        }

        // Ambil data yang dipassing melalui Safe Args
        val city = args.city
        val price = args.price
        val rating = args.rating
        val category = args.category

        // Membuat request untuk fetch rekomendasi
        val request = RecommendationRequest(city, price, rating, category)
        resultViewModel.fetchRecommendations(request)

        // Observasi perubahan data
        resultViewModel.recommendations.observe(viewLifecycleOwner) { recommendations ->
            adapter.submitList(recommendations)
        }

        // Observasi error
        resultViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}
