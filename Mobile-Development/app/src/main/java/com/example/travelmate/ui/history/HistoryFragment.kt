package com.example.travelmate.ui.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelmate.R
import com.example.travelmate.api.ApiClient
import com.example.travelmate.api.HomeApiService
import com.example.travelmate.api.PlaceResponse
import com.example.travelmate.data.response.HistoryResponse
import com.example.travelmate.data.retrofit.ApiService
import com.example.travelmate.databinding.FragmentHistoryBinding
import com.example.travelmate.databinding.FragmentHomeBinding
import com.example.travelmate.ui.home.HomeFragmentDirections
import com.example.travelmate.ui.home.PlaceAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        apiService = ApiClient.retrofit.create(ApiService::class.java)

        setupRecyclerView()

        getHistory()

        return binding.root
    }

    private fun setupRecyclerView() {
        // Mengatur layout manager dan adapter awal
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
//        binding.recyclerViewHistory.adapter = HistoryAdapter(emptyList()) { placeId ->
//            onPlaceClicked(placeId)
//        }
    }



    private fun getHistory() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null).let { "Bearer $it" }
        apiService.getHistory(token).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(call: Call<HistoryResponse>, response: Response<HistoryResponse>) {
                if (response.isSuccessful) {
                    val response = response.body()
                    if (response != null && response.history.isNotEmpty()) {

                        binding.recyclerViewHistory.adapter = HistoryAdapter(response.history)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No places available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load data: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("HomeFragment", "Error fetching data: ${t.localizedMessage}")
            }
        })
    }
//    private fun onPlaceClicked(placeId: String) {
//        val action = HistoryFragmentDirections.actionHistoryToDetail(placeId)
//        findNavController().navigate(action)
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}