package com.example.travelmate.ui.notifications

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.travelmate.R
import com.example.travelmate.databinding.FragmentNotificationsBinding
import com.example.travelmate.ui.login.LoginActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import org.json.JSONObject

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val client = OkHttpClient()
    private var profileUrl: String? = null

    // Activity Result Launcher untuk memilih gambar
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { uploadImage(it) }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonLogout.setOnClickListener {
            logoutUser()
        }

        binding.button3.setOnClickListener {
            val action = NotificationsFragmentDirections.actionProfileToHistory()
            findNavController().navigate(action)
        }

        binding.button4.setOnClickListener {
            val action = NotificationsFragmentDirections.actionProfileToFavorite()
            findNavController().navigate(action)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView5.text = "Loading..."
        getInfo()

        // Tambahkan listener untuk unggah gambar
        binding.imageView3.setOnClickListener {
            if (profileUrl.isNullOrEmpty()) {
                chooseImage()
            } else {
                Toast.makeText(requireContext(), "Foto profil sudah ada.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logoutUser() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPrefs", AppCompatActivity.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("auth_token")
            apply()
        }

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        requireActivity().finish()
    }

    private fun chooseImage() {
        pickImageLauncher.launch("image/*") // Memilih hanya gambar
    }

    private fun uploadImage(imageUri: Uri) {
        val contentResolver = requireActivity().contentResolver
        val inputStream = contentResolver.openInputStream(imageUri)
        val imageBytes = inputStream?.readBytes()
        inputStream?.close()

        if (imageBytes == null || imageBytes.size > 1048576) {
            // Ukuran file lebih dari 1 MB
            Toast.makeText(requireContext(), "Gambar terlalu besar. Pilih gambar dengan ukuran maksimum 1 MB.", Toast.LENGTH_LONG).show()
            return
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                "profile_image.jpg",
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageBytes)
            )
            .build()

        val sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Token tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = Request.Builder()
            .url("https://travelmate-capstone.et.r.appspot.com/home/profile/image")
            .addHeader("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("UploadError", "Gagal mengunggah: ${e.message}", e)
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Gagal mengunggah gambar. Periksa koneksi Anda.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Gambar berhasil diunggah!", Toast.LENGTH_SHORT).show()
                        getInfo() // Refresh informasi profil
                    } else {
                        val statusCode = response.code
                        val errorMessage = response.body?.string()

                        if (statusCode == 413) {
                            Toast.makeText(requireContext(), "Gambar terlalu besar. Maksimum ukuran adalah 1 MB.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(requireContext(), "Gagal mengunggah gambar. Error: $statusCode", Toast.LENGTH_SHORT).show()
                        }

                        Log.e("UploadError", "Error $statusCode: $errorMessage")
                    }
                }
            }
        })
    }

    private fun getInfo() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Token tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show()
            binding.textView5.text = "Gagal memuat data."
            return
        }

        val request = Request.Builder()
            .url("https://travelmate-capstone.et.r.appspot.com/home/profile")
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GetInfoError", "Gagal terhubung: ${e.message}", e)
                requireActivity().runOnUiThread {
                    binding.textView5.text = "Gagal terhubung. Coba lagi nanti."
                    Toast.makeText(requireContext(), "Gagal terhubung, periksa koneksi Anda.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBodyString = response.body?.string()
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(responseBodyString)
                            val dataObject = jsonObject.getJSONObject("data")
                            val name = dataObject.getString("name")
                            profileUrl = dataObject.optString("profileUrl", "")

                            binding.textView5.text = "Welcome, $name"

                            if (profileUrl.isNullOrEmpty()) {
                                binding.imageViewEdit.visibility = View.VISIBLE
                                binding.imageView3.setImageResource(R.drawable.profile_1)
                            } else {
                                binding.imageViewEdit.visibility = View.GONE
                                Glide.with(requireContext())
                                    .load(profileUrl)
                                    .placeholder(R.drawable.profile_1)
                                    .error(R.drawable.profile_2)
                                    .into(binding.imageView3)
                            }
                        } catch (e: Exception) {
                            Log.e("GetInfoError", "Error parsing JSON: ${e.message}", e)
                            binding.textView5.text = "Terjadi kesalahan saat memuat data."
                        }
                    } else {
                        binding.textView5.text = "Gagal memuat data. Coba lagi nanti."
                    }
                }
            }
        })
    }
}
