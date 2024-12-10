package com.example.travelmate.data.repository

import com.example.travelmate.data.model.RecommendationRequest
import com.example.travelmate.data.retrofit.ApiService
import com.example.travelmate.data.response.RecommendationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecommendationRepository(private val apiService: ApiService) {

    // Fungsi untuk mendapatkan rekomendasi dari API
    fun getRecommendations(
        request: RecommendationRequest,
        onSuccess: (RecommendationResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.getRecommendations(request).enqueue(object : Callback<RecommendationResponse> {
            override fun onResponse(
                call: Call<RecommendationResponse>,
                response: Response<RecommendationResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onSuccess(it)
                    }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RecommendationResponse>, t: Throwable) {
                onError("Network Error: ${t.message}")
            }
        })
    }
}
