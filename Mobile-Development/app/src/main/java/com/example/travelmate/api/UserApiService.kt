package com.example.travelmate.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class SignInRequest(val email: String, val password: String)
data class SignInResponse(val status: String, val message: String, val token: String)

data class SignUpRequest(val name: String, val email: String, val password: String)
data class SignUpResponse(val status: String, val message: String, val id: String, val token: String)

interface UserApiService {
    @POST("/signin")
    fun signIn(@Body request: SignInRequest): Call<SignInResponse>

    @POST("/signup")
    fun signUp(@Body request: SignUpRequest): Call<SignUpResponse>
}

interface HomeApiService {
    @GET("/home")
    fun getPlaces(): Call<PlaceResponse>
}

