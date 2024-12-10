package com.example.travelmate.ui.detail

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.travelmate.MainActivity
import com.example.travelmate.R
import com.example.travelmate.api.ApiClient
import com.example.travelmate.api.ErrorResponse
import com.example.travelmate.data.local.database.FavoriteDatabase
import com.example.travelmate.data.local.entity.FavoriteEntity
import com.example.travelmate.data.model.DateRequest
import com.example.travelmate.data.repository.FavoriteRepository
import com.example.travelmate.data.response.DateResponse
import com.example.travelmate.data.response.DetailResponse
import com.example.travelmate.data.response.Main
import com.example.travelmate.data.response.WeatherItem
import com.example.travelmate.data.retrofit.ApiService
import com.example.travelmate.databinding.FragmentDetailBinding
import com.example.travelmate.ui.favorite.FavoriteViewModel
import com.example.travelmate.ui.favorite.FavoriteViewModelFactory
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiService: ApiService
    private lateinit var favoriteViewModel: FavoriteViewModel

    private var isBookmarked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        // Initialize Retrofit API service
        apiService = ApiClient.retrofit.create(ApiService::class.java)

        val application = requireNotNull(this.activity).application
        val repository = FavoriteRepository(FavoriteDatabase.getDatabase(application).favoriteDao())
        val viewModelFactory = FavoriteViewModelFactory(repository)
        favoriteViewModel = ViewModelProvider(this, viewModelFactory)[FavoriteViewModel::class.java]

        val args = DetailFragmentArgs.fromBundle(requireArguments())
        val placeId = args.placeId

        if (placeId != -1) {
            // Fetch the details of the place using the placeId
            fetchPlaceDetails(placeId)
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.postButton.setOnClickListener {
            val dateTime = binding.date.text.toString()
            if (dateTime.isNotEmpty()){
                storeDate(dateTime, placeId)
            }
            else{
                Toast.makeText(requireContext(), "Please Input Date", Toast.LENGTH_SHORT).show()
//                Log.d("API_RESPONSE", "Response Code: ${response.code()}")
            }
        }

        // Set initial state of bookmark
        favoriteViewModel.isFavorite(placeId).observe(viewLifecycleOwner) { isFavorite ->
            isBookmarked = isFavorite
            updateBookmarkIcon()
        }

        // Handle bookmark click
        binding.bookmarkIcon.setOnClickListener {
            val place = FavoriteEntity(
                id = placeId,
                name = binding.nameTextView.text.toString(),
                city = binding.cityTextView.text.toString(),
                address = binding.addressTextView.text.toString(),
                category = binding.categoryTextView.text.toString(),
                price = binding.priceTextView.text.toString(),
                rating = binding.ratingTextView.text.toString().toFloatOrNull() ?: 0f
            )

            if (isBookmarked) {
                favoriteViewModel.deleteFavorite(place)
                Toast.makeText(requireContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show()
            } else {
                favoriteViewModel.insertFavorite(place)
                Toast.makeText(requireContext(), "Added to Favorites", Toast.LENGTH_SHORT).show()
            }

            isBookmarked = !isBookmarked // Toggle state bookmark
            updateBookmarkIcon()
        }

        binding.date.setOnClickListener {
            showDatePickerDialog()
        }

        return binding.root
    }

    private fun showDatePickerDialog() {
        // Get current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Format selected date in ISO 8601 format (yyyy-MM-dd)
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth)
                binding.date.text = selectedDate // Update TextView with the selected date
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun storeDate(date: String, placeId: Int) {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null).let { "Bearer $it" }
        Log.d("Token", "Token: $token")
        val request = DateRequest(date)
        apiService.postActivity(request, placeId, token.toString()).enqueue(object : Callback<DateResponse> {
            override fun onResponse(call: Call<DateResponse>, response: Response<DateResponse>) {
                if (response.isSuccessful){
                    val body = response.body()
                    if (body != null && body.status == "success") {
                        Toast.makeText(requireContext(), body.message, Toast.LENGTH_SHORT).show()

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

    private fun fetchPlaceDetails(placeId: Int) {
        apiService.getDetailPage(placeId).enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                if (response.isSuccessful) {
                    val placeDetails = response.body()

                    if (placeDetails != null) {
                        // Update the UI with the fetched details
                        binding.nameTextView.text = placeDetails.name
                        binding.cityTextView.text = placeDetails.city
                        binding.priceTextView.text = placeDetails.price
                        binding.ratingTextView.text = placeDetails.rating.toString()
                        binding.categoryTextView.text = placeDetails.category
                        binding.addressTextView.text = placeDetails.address

                        updateWeather(placeDetails.weatherResponse.weather, placeDetails.weatherResponse.main)
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun covertToCelcius(temp: Float): String {
        val celsius = temp - 273.15
        return String.format("%.2f", celsius)
    }

    private fun updateWeather(
        weatherList: List<WeatherItem>,
        weaterTemp: Main
    ) {
        // Assuming you want to display the weather information in the UI
        if (weatherList.isNotEmpty()) {
            val weather = weatherList[0]
            val temp = covertToCelcius(weaterTemp.temp)
            val feelTemp = covertToCelcius(weaterTemp.feelsLike)
            binding.weatherDescriptionTextView.text = weather.description
            binding.tempTextView.text = "Tempratur: $temp°C"
            binding.feelTempTextView.text = "Feel like: $feelTemp°C"

            val iconUrl = "https://openweathermap.org/img/wn/${weather.icon}@2x.png"

            Glide.with(requireContext())
                .load(iconUrl)
                .into(binding.weatherIconImageView)
        }
    }

    private fun updateBookmarkIcon() {
        val iconRes = if (isBookmarked) {
            R.drawable.baseline_bookmark_24 // Ikon filled
        } else {
            R.drawable.baseline_bookmark_border_24 // Ikon outline
        }
        binding.bookmarkIcon.setImageResource(iconRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
