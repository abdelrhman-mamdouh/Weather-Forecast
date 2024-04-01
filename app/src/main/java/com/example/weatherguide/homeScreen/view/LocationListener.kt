package com.example.weatherguide.homeScreen.view

import com.example.weatherguide.model.SharedFlowObject

interface LocationListener {
    fun onLocationChanged(sharedFlowObject: SharedFlowObject)

}