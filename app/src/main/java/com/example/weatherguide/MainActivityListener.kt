package com.example.weatherguide

import java.util.concurrent.locks.Condition

interface MainActivityListener {
    fun updateBackgroundAnimation(condition: String)
}