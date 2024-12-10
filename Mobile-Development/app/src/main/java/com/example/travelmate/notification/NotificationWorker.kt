package com.example.travelmate.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.travelmate.api.ApiClient
import com.example.travelmate.data.response.ProgressItem
import com.example.travelmate.data.response.ProgressResponse
import com.example.travelmate.data.retrofit.ApiService
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.travelmate.MainActivity
import com.example.travelmate.R

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private val apiService: ApiService = ApiClient.retrofit.create(ApiService::class.java)

    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)?.let { "Bearer $it" }

        return try {
            val response: Response<ProgressResponse> = apiService.getProgress(token.toString()).execute()

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.progress.isNotEmpty()) {
                    checkAndNotify(applicationContext, apiResponse.progress)
                }
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun checkAndNotify(context: Context, progressList: List<ProgressItem>) {
        for (item in progressList) {
            if (isToday(item.date)) {
                if (isDuplicate(context, item.id)){
                    sendNotification(
                        context,
                        "Hari yang Kamu Tunggu Telah Tiba, Jangan Lewatkan Keseruannya!!",
                        "hari ini kamu ada rencana berkunjung ke ${item.name} yang terletak di ${item.city}, ayo bersiap!!"
                    )
                }
            }
        }
    }

    private fun isDuplicate(context: Context, notificationId: String): Boolean {
        val sharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val lastNotified = sharedPreferences.getString(notificationId, null)

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return if (lastNotified != currentDate) {
            sharedPreferences.edit().putString(notificationId, currentDate).apply()
            true
        } else {
            false
        }
    }

    private fun isToday(dateString: String): Boolean {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val givenDate = formatter.parse(dateString)
        val currentDate = formatter.parse(formatter.format(Date()))

        return givenDate == currentDate
    }

    private fun sendNotification(context: Context, title: String, message: String) {
        val channelId = "travel_mate_channel"
        val channelName = "Travel Mate Notifications"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.baseline_notifications_none_24)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
