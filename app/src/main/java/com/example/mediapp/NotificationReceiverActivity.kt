package com.example.mediapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat

class NotificationActivity : AppCompatActivity() {

    private val NOTIFICATION_PERMISSION_CODE = 1001
    private val CHANNEL_ID = "meditation_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_receiver)

        val btnNotify = findViewById<Button>(R.id.btn_notify)
        btnNotify.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 ve üstü: izin kontrolü yap
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    showNotification()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_CODE
                    )
                }
            } else {
                // Android 12 ve altı: direkt göster
                showNotification()
            }
        }
    }

    private fun showNotification() {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Kanal oluştur (Android 8+ için)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Meditation Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_meditation) // kendi ikonun olmalı
            .setContentTitle("Meditasyon Zamanı!")
            .setContentText("Kendin için birkaç dakika ayırmayı unutma.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
        Toast.makeText(this, "Bildirim gönderildi", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showNotification()
            } else {
                Toast.makeText(this, "Bildirim izni reddedildi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
