package com.example.travelmate.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

object NotificationPermission {
    fun isPermitted(
        context: Context,
        requestPermissionLauncher: ActivityResultLauncher<String>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                scheduleNotify(context)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            scheduleNotify(context)
        }
    }

    fun handlePermission(context: Context, isGranted: Boolean) {
        if (isGranted) {
            Toast.makeText(context, "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show()
            scheduleNotify(context)
        } else {
            Toast.makeText(context, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scheduleNotify(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(5, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "TravelMateNotification",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}