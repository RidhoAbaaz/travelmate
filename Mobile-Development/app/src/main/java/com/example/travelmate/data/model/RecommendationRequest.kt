package com.example.travelmate.data.model

data class RecommendationRequest(
    val city: String,
    val price: Int,
    val rating: Float,
    val category: String
)
