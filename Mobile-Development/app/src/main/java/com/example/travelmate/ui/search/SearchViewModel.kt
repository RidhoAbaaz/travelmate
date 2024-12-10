package com.example.travelmate.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelmate.data.model.RecommendationRequest

class SearchViewModel : ViewModel() {

    private val _searchData = MutableLiveData<RecommendationRequest>()
    val searchData: LiveData<RecommendationRequest> = _searchData

    // Set the search data based on user input
    fun setSearchData(city: String, price: Int, rating: Float, category: String) {
        _searchData.value = RecommendationRequest(city, price, rating, category)
    }
}
