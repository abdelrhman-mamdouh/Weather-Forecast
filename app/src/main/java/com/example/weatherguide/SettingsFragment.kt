package com.example.weatherguide

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.weatherguide.mapScreen.view.MapActivity


class SettingsFragment : Fragment() {
    private lateinit var locationRadioGroup: RadioGroup
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        sharedPreferences = requireContext().getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
        val selectedOption = sharedPreferences.getString("selected_option", null)

        locationRadioGroup = view.findViewById(R.id.locationRadioGroup)
        when (selectedOption) {
            "Map" -> locationRadioGroup.check(R.id.radioButtonMap)
            "GPS" -> locationRadioGroup.check(R.id.radioButtonGPS)
        }

        locationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButtonMap -> {
                    sharedPreferences.edit().putString("selected_option", "Map").apply()
                    val intent = Intent(requireContext(), MapActivity::class.java)
                    startActivity(intent)
                }
                R.id.radioButtonGPS -> {
                    sharedPreferences.edit().putString("selected_option", "GPS").apply()
                }
            }
        }

        return view
    }
}

