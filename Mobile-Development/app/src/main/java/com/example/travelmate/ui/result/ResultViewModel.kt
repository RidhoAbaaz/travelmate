package com.example.travelmate.ui.result

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelmate.data.model.RecommendationRequest
import com.example.travelmate.data.repository.RecommendationRepository
import com.example.travelmate.data.response.DataItem

class ResultViewModel(private val repository: RecommendationRepository) : ViewModel() {

    private val _recommendations = MutableLiveData<List<DataItem>>()
    val recommendations: LiveData<List<DataItem>> get() = _recommendations

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Fungsi untuk mengambil rekomendasi wisata
    fun fetchRecommendations(request: RecommendationRequest) {
        repository.getRecommendations(request,
            onSuccess = { response ->
                Log.d("API_RESPONSE", "Data received: ${response.data}")
                _recommendations.value = response.data
            },
            onError = { message ->
                Log.e("API_ERROR", "Error: $message")
                _error.value = message
            }
        )
    }
}
