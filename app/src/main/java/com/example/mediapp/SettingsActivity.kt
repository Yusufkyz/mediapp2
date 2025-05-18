package com.example.mediapp

import android.app.*
import android.content.*
import android.os.*
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import java.util.Calendar

// SettingsActivity.kt
class SettingsActivity : AppCompatActivity() {

    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var timePickerButton: Button
    private lateinit var spinnerDuration: Spinner
    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var aboutButton: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        notificationSwitch = findViewById(R.id.switch_notifications)
        timePickerButton = findViewById(R.id.button_pick_time)
        spinnerDuration = findViewById(R.id.spinner_meditation_duration)
        darkModeSwitch = findViewById(R.id.switch_dark_mode)
        aboutButton = findViewById(R.id.button_about)

        sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        setupUI()
    }

    private fun setupUI() {
        // Yüklenen ayarlar
        notificationSwitch.isChecked = sharedPreferences.getBoolean("notifications_enabled", false)
        darkModeSwitch.isChecked = sharedPreferences.getBoolean("dark_mode", false)

        // Bildirim Switch
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply()
            if (isChecked) {
                scheduleDailyNotification(this)
            } else {
                cancelDailyNotification(this)
            }
        }

        // TimePicker
        timePickerButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                // Saati kaydet
                sharedPreferences.edit()
                    .putInt("notification_hour", selectedHour)
                    .putInt("notification_minute", selectedMinute)
                    .apply()

                timePickerButton.text = "Hatırlatma Saati: ${"%02d:%02d".format(selectedHour, selectedMinute)}"
                scheduleDailyNotification(this)
            }, hour, minute, true)

            timePickerDialog.show()
        }

        // Spinner: Süre Seçimi
        val durations = listOf("10 Dakika", "15 Dakika", "20 Dakika")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDuration.adapter = adapter

        // Spinner seçimi kaydetme
        val savedPosition = sharedPreferences.getInt("meditation_duration_position", 0)
        spinnerDuration.setSelection(savedPosition)

        spinnerDuration.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sharedPreferences.edit().putInt("meditation_duration_position", position).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Karanlık Mod Switch
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
            recreate() // değişikliği uygulamak için activity'i yeniden başlat
        }

        // Hakkında Button
        aboutButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hakkında")
                .setMessage("Meditasyon uygulaması v1.0\nİletişim: destek@meditasyonapp.com")
                .setPositiveButton("Kapat", null)
                .show()
        }
    }

    private fun scheduleDailyNotification(context: Context) {
        val notificationEnabled = sharedPreferences.getBoolean("notifications_enabled", false)
        if (!notificationEnabled) return

        val hour = sharedPreferences.getInt("notification_hour", 8)
        val minute = sharedPreferences.getInt("notification_minute", 0)

        val intent = Intent(context,NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelDailyNotification(context: Context) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
