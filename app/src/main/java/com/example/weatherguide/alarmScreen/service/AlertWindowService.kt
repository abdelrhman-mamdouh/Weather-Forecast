package com.example.weatherguide.alarmScreen.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.weatherguide.MainActivity
import com.example.weatherguide.R
import com.example.weatherguide.alarmScreen.viewModel.AlarmViewModel
import com.example.weatherguide.alarmScreen.viewModel.AlarmViewModelFactory
import com.example.weatherguide.db.WeatherLocalDataSourceImpl
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.network.WeatherRemoteSourceDataImpl

class AlertWindowService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var mediaPlayer: MediaPlayer
    private var message: String = ""
    private lateinit var alarmViewModelFactory: AlarmViewModelFactory
    private lateinit var alarmViewModel:AlarmViewModel
    override fun onCreate() {
        super.onCreate()

        alarmViewModelFactory = AlarmViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherRemoteSourceDataImpl.getInstance(),
                WeatherLocalDataSourceImpl(this)
            )
        )
        val viewModelStore = ViewModelStore()
        alarmViewModel = ViewModelProvider(viewModelStore, alarmViewModelFactory).get(AlarmViewModel::class.java)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.custom_alert, null)
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        mediaPlayer.isLooping = true

        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.x = 0
        params.y = 0
        windowManager.addView(floatingView, params)

        val dismissButton: Button = floatingView.findViewById(R.id.btnSave)
        dismissButton.setOnClickListener {
            stopSelf()

        }
        val rootView: View = floatingView.findViewById(R.id.root_layout)
        rootView.setOnClickListener {
            val intent = Intent(this@AlertWindowService, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val receivedMessage = intent?.getStringExtra("message")
        Log.i("TAG", "onStartCommand:${receivedMessage} ")
        val alarmId = intent?.getLongExtra("alarmId", -1)
        if (alarmId != null) {
            alarmViewModel.removeAlarmById(alarmId)
        }
        message = receivedMessage ?: ""
        val messageTextView: TextView = floatingView.findViewById(R.id.textMessage)
        messageTextView.text = message
        mediaPlayer.start()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer.stop()
        mediaPlayer.release()
        windowManager.removeView(floatingView)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}

