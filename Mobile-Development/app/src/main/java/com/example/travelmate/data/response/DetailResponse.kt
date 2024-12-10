package com.example.travelmate.data.response

import com.google.gson.annotations.SerializedName

data class DetailResponse(

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("weatherResponse")
	val weatherResponse: WeatherResponse,

	@field:SerializedName("price")
	val price: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("rating")
	val rating: Any,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("category")
	val category: String,

	@field:SerializedName("status")
	val status: String
)

data class WeatherResponse(

	@field:SerializedName("coord")
	val coord: Coord,

	@field:SerializedName("weather")
	val weather: List<WeatherItem>,

	@field:SerializedName("main")
	val main: Main,

	@field:SerializedName("base")
	val base: String
)

data class Coord(

	@field:SerializedName("lon")
	val lon: Any,

	@field:SerializedName("lat")
	val lat: Any
)

data class WeatherItem(

	@field:SerializedName("icon")
	val icon: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("main")
	val main: String,

	@field:SerializedName("id")
	val id: Int
)

data class Main(

	@field:SerializedName("temp")
	val temp: Float,

	@field:SerializedName("temp_min")
	val tempMin: Any,

	@field:SerializedName("grnd_level")
	val grndLevel: Int,

	@field:SerializedName("humidity")
	val humidity: Int,

	@field:SerializedName("pressure")
	val pressure: Int,

	@field:SerializedName("sea_level")
	val seaLevel: Int,

	@field:SerializedName("feels_like")
	val feelsLike: Float,

	@field:SerializedName("temp_max")
	val tempMax: Any
)
