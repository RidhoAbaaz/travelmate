package com.example.travelmate.data.retrofit

import com.example.travelmate.data.model.DateRequest
import com.example.travelmate.data.model.RecommendationRequest
import com.example.travelmate.data.response.DataItem
import com.example.travelmate.data.response.DateResponse
import com.example.travelmate.data.response.DetailResponse
import com.example.travelmate.data.response.HistoryResponse
import com.example.travelmate.data.response.ProgressResponse
import com.example.travelmate.data.response.RecommendationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {
    @POST("/home/recomendation")
    fun getRecommendations(
        @Body request: RecommendationRequest
    ): Call<RecommendationResponse>

    @GET("/home/{id}")
    fun getDetailPage (
        @Path("id") id: Int
    ): Call<DetailResponse>

    @POST("/home/activities/{placeId}")
    fun postActivity(
        @Body request: DateRequest,
        @Path("placeId") placeId : Int,
        @Header("Authorization") token: String
    ): Call<DateResponse>

    @GET("/home/progress")
    fun getProgress(
        @Header("Authorization") token: String
    ): Call<ProgressResponse>

    @DELETE("/home/progress/{progressId}")
    fun deleteProgress(
        @Header("Authorization") token: String,
        @Path("progressId") progressId : String
    ): Call<DateResponse>

    @GET("/home/history")
    fun getHistory(
        @Header("Authorization") token: String
    ): Call<HistoryResponse>
}