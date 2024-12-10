package com.example.travelmate.data.response

import com.google.gson.annotations.SerializedName

data class ProgressResponse(

	@field:SerializedName("progress")
	val progress: List<ProgressItem>,

	@field:SerializedName("status")
	val status: String
)

data class HistoryResponse(
	@field:SerializedName("history")
	val history: List<ProgressItem>,

	@field:SerializedName("status")
	val status: String
)

data class ProgressItem(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("insertedAt")
	val insertedAt: String,

	@field:SerializedName("price")
	val price: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("rating")
	val rating: Any,

	@field:SerializedName("updateAt")
	val updateAt: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("category")
	val category: String
)
