package com.example.weatherguide.alarmScreen.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.weatherguide.alarmScreen.service.AlertWindowService

class DismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.stopService(Intent(context, AlertWindowService::class.java))
    }
}