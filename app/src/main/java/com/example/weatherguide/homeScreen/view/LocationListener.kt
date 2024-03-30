package com.example.weatherguide.homeScreen.view

import android.location.Location
import com.example.weatherguide.model.SharedFlowObject

interface LocationListener {
    fun onLocationChanged(sharedFlowObject: SharedFlowObject)

}