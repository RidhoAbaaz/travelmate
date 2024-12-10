package com.example.travelmate.ui.login
import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelmate.MainActivity
import com.example.travelmate.api.ApiClient
import com.example.travelmate.api.SignInRequest
import com.example.travelmate.api.SignInResponse
import com.example.travelmate.api.ErrorResponse
import com.example.travelmate.databinding.ActivityLoginBinding
import com.example.travelmate.ui.register.RegisterActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol untuk berpindah ke halaman Register
        binding.textView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Tombol untuk Login
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Empty Fields Are Not Allowed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(email: String, password: String) {
        val request = SignInRequest(email, password)

        ApiClient.userApiService.signIn(request).enqueue(object : Callback<SignInResponse> {
            override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                Log.d("API_RESPONSE", "Response Code: ${response.code()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("API_RESPONSE", "Body: $body")

                    if (body != null && body.status == "success") {
                        Toast.makeText(this@LoginActivity, body.message, Toast.LENGTH_SHORT).show()

                        // Simpan token jika diperlukan
                        saveToken(body.token)

                        // Berpindah ke MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed: ${body?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("API_RESPONSE", "Error Body: $errorBody")

                    val gson = Gson()
                    val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)

                    Toast.makeText(this@LoginActivity, "Error: ${errorResponse.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                Log.e("API_RESPONSE", "Failure: ${t.message}")
                Toast.makeText(this@LoginActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()
    }

    override fun onStart() {
        super.onStart()

        // Periksa apakah token sudah ada (sudah login sebelumnya)
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token != null) {
            // Jika sudah login, langsung berpindah ke MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
