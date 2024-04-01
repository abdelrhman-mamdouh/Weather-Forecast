package com.example.weatherguide

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.view.View
import com.google.android.material.snackbar.Snackbar

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            val rootView = (context as MainActivity).findViewById<View>(android.R.id.content)
            Snackbar.make(rootView, "No network connection", Snackbar.LENGTH_LONG).show()
        }
    }
}