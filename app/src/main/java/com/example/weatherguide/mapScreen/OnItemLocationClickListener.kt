package com.example.weatherguide.mapScreen

interface OnItemLocationClickListener {
    fun onClick(latitude: Double, longitude: Double, locatonName: String)
}