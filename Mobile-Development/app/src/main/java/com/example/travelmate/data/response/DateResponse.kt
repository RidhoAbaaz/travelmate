package com.example.travelmate.data.response

import com.google.gson.annotations.SerializedName

data class DateResponse(

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)
