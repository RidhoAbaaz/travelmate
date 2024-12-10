package com.example.travelmate.ui.progress

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelmate.api.ApiClient
import com.example.travelmate.api.ErrorResponse
import com.example.travelmate.data.response.DateResponse
import com.example.travelmate.data.response.ProgressResponse
import com.example.travelmate.data.retrofit.ApiService
import com.example.travelmate.databinding.FragmentProgressBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log


class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiService: ApiService
    private lateinit var progressAdapter: ProgressAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentProgressBinding.inflate(inflater, container, false)
        apiService = ApiClient.retrofit.create(ApiService::class.java)

        setupRecyclerView()

        getProgressData()

        return binding.root
    }

    private fun setupRecyclerView() {
        progressAdapter = ProgressAdapter(mutableListOf()) { placeId ->
            deleteData(placeId)
        }
        progressAdapter.notifyDataSetChanged()
        binding.recyclerViewProgress.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProgress.adapter = progressAdapter
    }

    private fun getProgressData() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null).let { "Bearer $it" }
        Log.d("TOKEN", "token $token")
        apiService.getProgress(token).enqueue(object : Callback<ProgressResponse> {
            override fun onResponse(call: Call<ProgressResponse>, response: Response<ProgressResponse>) {
                if (response.isSuccessful) {
                    val response = response.body()
                    if (response != null && response.progress.isNotEmpty()) {

                        progressAdapter = ProgressAdapter(response.progress.toMutableList()) { placeId ->
                            deleteData(placeId)
                        }
                        binding.recyclerViewProgress.adapter = progressAdapter
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "you don't reserve any place",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "you don't reserve any place",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ProgressResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun deleteData(placeId: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null).let { "Bearer $it" }
        Log.d("TOKEN", "token $token")
        Log.d("ID", "Id $placeId")
        apiService.deleteProgress(token, placeId).enqueue(object : Callback<DateResponse> {
            override fun onResponse(call: Call<DateResponse>, response: Response<DateResponse>) {
                if (response.isSuccessful){
                    val body = response.body()
                    if (body != null && body.status == "success") {
                        Toast.makeText(requireContext(), body.message, Toast.LENGTH_SHORT).show()
                        val position = progressAdapter.places.indexOfFirst { it.id == placeId }
                        if (position != -1) {
                            progressAdapter.removeItem(position) // Hapus item dari adapter
                        }
                    } else {
                        Toast.makeText(requireContext(), "Upload Failed: ${body?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("API_RESPONSE", "Error Body: $errorBody")

                    Toast.makeText(requireContext(), "Error: Your Session is expired please login", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DateResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}